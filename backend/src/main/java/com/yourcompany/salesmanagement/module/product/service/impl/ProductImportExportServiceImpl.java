package com.yourcompany.salesmanagement.module.product.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.auth.service.dto.UserPrincipal;
import com.yourcompany.salesmanagement.module.importjob.dto.response.ImportJobResponse;
import com.yourcompany.salesmanagement.module.importjob.entity.ImportJob;
import com.yourcompany.salesmanagement.module.importjob.repository.ImportJobRepository;
import com.yourcompany.salesmanagement.module.product.entity.Product;
import com.yourcompany.salesmanagement.module.product.repository.ProductRepository;
import com.yourcompany.salesmanagement.module.product.service.ProductImportExportService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductImportExportServiceImpl implements ProductImportExportService {
    private static final String TYPE_PRODUCT = "PRODUCT";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_RUNNING = "RUNNING";
    private static final String STATUS_SUCCEEDED = "SUCCEEDED";
    private static final String STATUS_FAILED = "FAILED";

    private final ImportJobRepository importJobRepository;
    private final ProductRepository productRepository;

    public ProductImportExportServiceImpl(ImportJobRepository importJobRepository, ProductRepository productRepository) {
        this.importJobRepository = importJobRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ImportJobResponse startImport(MultipartFile file) {
        Long storeId = SecurityUtils.requireStoreId();
        UserPrincipal principal = SecurityUtils.requirePrincipal();
        if (file == null || file.isEmpty()) {
            throw new BusinessException("file is required", HttpStatus.BAD_REQUEST);
        }

        ImportJob j = new ImportJob();
        j.setStoreId(storeId);
        j.setType(TYPE_PRODUCT);
        j.setStatus(STATUS_PENDING);
        j.setOriginalFilename(file.getOriginalFilename());
        j.setContentType(file.getContentType());
        j.setCreatedBy(principal.userId());
        j.setTotalRows(0);
        j.setSuccessRows(0);
        j.setFailedRows(0);
        j = importJobRepository.save(j);

        // Read content into memory for async processing (foundation; can be replaced with file storage later)
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (Exception e) {
            throw new BusinessException("Failed to read upload file", HttpStatus.BAD_REQUEST);
        }

        processImportAsync(j.getId(), storeId, bytes);
        return toResponse(j);
    }

    @Override
    public Resource exportCsv() {
        Long storeId = SecurityUtils.requireStoreId();
        List<Product> products = productRepository.findAllByStoreId(storeId);
        StringBuilder sb = new StringBuilder();
        sb.append("sku,name,categoryId,supplierId,sellingPrice,trackInventory,status\n");
        for (Product p : products) {
            sb.append(escape(p.getSku())).append(',')
              .append(escape(p.getName())).append(',')
              .append(p.getCategoryId() == null ? "" : p.getCategoryId()).append(',')
              .append(p.getSupplierId() == null ? "" : p.getSupplierId()).append(',')
              .append(p.getSellingPrice() == null ? "0.00" : p.getSellingPrice().setScale(2, RoundingMode.HALF_UP)).append(',')
              .append(p.getTrackInventory() == null ? "true" : p.getTrackInventory()).append(',')
              .append(p.getStatus() == null ? "" : p.getStatus())
              .append('\n');
        }
        return new ByteArrayResource(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Async
    @Transactional
    protected void processImportAsync(Long jobId, Long storeId, byte[] bytes) {
        ImportJob j = importJobRepository.findById(jobId).orElse(null);
        if (j == null) return;
        if (!storeId.equals(j.getStoreId())) return;

        j.setStatus(STATUS_RUNNING);
        j.setStartedAt(LocalDateTime.now());
        importJobRepository.save(j);

        int total = 0;
        int ok = 0;
        int fail = 0;
        List<String> errors = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new java.io.ByteArrayInputStream(bytes), StandardCharsets.UTF_8))) {
            String header = br.readLine();
            if (header == null) {
                throw new BusinessException("Empty file", HttpStatus.BAD_REQUEST);
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                total++;
                try {
                    // Foundation CSV parser: split by comma without advanced quoting rules
                    String[] cols = line.split(",", -1);
                    String sku = get(cols, 0);
                    String name = get(cols, 1);
                    Long categoryId = parseLong(get(cols, 2));
                    Long supplierId = parseLong(get(cols, 3));
                    BigDecimal sellingPrice = parseMoney(get(cols, 4));
                    Boolean trackInventory = parseBoolean(get(cols, 5));
                    String status = get(cols, 6);

                    if (sku == null || sku.isBlank()) {
                        throw new BusinessException("sku is required", HttpStatus.BAD_REQUEST);
                    }
                    if (name == null || name.isBlank()) {
                        throw new BusinessException("name is required", HttpStatus.BAD_REQUEST);
                    }

                    Product p = productRepository.findFirstByStoreIdAndSku(storeId, sku.trim())
                            .orElseGet(Product::new);
                    p.setStoreId(storeId);
                    p.setSku(sku.trim());
                    p.setName(name.trim());
                    p.setCategoryId(categoryId);
                    p.setSupplierId(supplierId);
                    p.setSellingPrice(sellingPrice == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : sellingPrice);
                    p.setTrackInventory(trackInventory == null ? Boolean.TRUE : trackInventory);
                    p.setStatus(status == null || status.isBlank() ? "ACTIVE" : status.trim());
                    productRepository.save(p);
                    ok++;
                } catch (Exception ex) {
                    fail++;
                    errors.add("Row " + total + ": " + ex.getMessage());
                }
            }

            j.setTotalRows(total);
            j.setSuccessRows(ok);
            j.setFailedRows(fail);
            j.setStatus(fail > 0 ? STATUS_FAILED : STATUS_SUCCEEDED);
            j.setErrorMessage(errors.isEmpty() ? null : String.join(" | ", errors).substring(0, Math.min(1000, String.join(" | ", errors).length())));
            j.setFinishedAt(LocalDateTime.now());
            importJobRepository.save(j);
        } catch (Exception e) {
            j.setTotalRows(total);
            j.setSuccessRows(ok);
            j.setFailedRows(Math.max(fail, 1));
            j.setStatus(STATUS_FAILED);
            j.setErrorMessage(e.getMessage() == null ? "Import failed" : e.getMessage().substring(0, Math.min(1000, e.getMessage().length())));
            j.setFinishedAt(LocalDateTime.now());
            importJobRepository.save(j);
        }
    }

    private ImportJobResponse toResponse(ImportJob j) {
        return new ImportJobResponse(
                j.getId(),
                j.getStoreId(),
                j.getType(),
                j.getStatus(),
                j.getOriginalFilename(),
                j.getTotalRows(),
                j.getSuccessRows(),
                j.getFailedRows(),
                j.getErrorMessage(),
                j.getCreatedAt(),
                j.getStartedAt(),
                j.getFinishedAt()
        );
    }

    private static String get(String[] cols, int idx) {
        if (cols == null || idx >= cols.length) return null;
        return cols[idx];
    }

    private static Long parseLong(String s) {
        if (s == null) return null;
        String v = s.trim();
        if (v.isEmpty()) return null;
        return Long.parseLong(v);
    }

    private static BigDecimal parseMoney(String s) {
        if (s == null) return null;
        String v = s.trim();
        if (v.isEmpty()) return null;
        return new BigDecimal(v).setScale(2, RoundingMode.HALF_UP);
    }

    private static Boolean parseBoolean(String s) {
        if (s == null) return null;
        String v = s.trim();
        if (v.isEmpty()) return null;
        return "true".equalsIgnoreCase(v) || "1".equals(v) || "yes".equalsIgnoreCase(v);
    }

    private static String escape(String s) {
        if (s == null) return "";
        String v = s.replace("\"", "\"\"");
        if (v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r")) {
            return "\"" + v + "\"";
        }
        return v;
    }
}


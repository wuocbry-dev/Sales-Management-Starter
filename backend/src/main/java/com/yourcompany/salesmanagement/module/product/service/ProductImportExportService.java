package com.yourcompany.salesmanagement.module.product.service;

import com.yourcompany.salesmanagement.module.importjob.dto.response.ImportJobResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ProductImportExportService {
    ImportJobResponse startImport(MultipartFile file);

    Resource exportCsv();
}


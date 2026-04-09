package com.yourcompany.salesmanagement.module.importjob.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.importjob.dto.response.ImportJobResponse;
import com.yourcompany.salesmanagement.module.importjob.entity.ImportJob;
import com.yourcompany.salesmanagement.module.importjob.repository.ImportJobRepository;
import com.yourcompany.salesmanagement.module.importjob.service.ImportJobService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ImportJobServiceImpl implements ImportJobService {
    private final ImportJobRepository importJobRepository;

    public ImportJobServiceImpl(ImportJobRepository importJobRepository) {
        this.importJobRepository = importJobRepository;
    }

    @Override
    public ImportJobResponse getById(Long id) {
        Long storeId = SecurityUtils.requireStoreId();
        ImportJob j = importJobRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Import job not found", HttpStatus.NOT_FOUND));
        return toResponse(j);
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
}


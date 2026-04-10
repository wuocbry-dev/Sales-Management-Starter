package com.yourcompany.salesmanagement.module.importjob.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.importjob.dto.response.ImportJobResponse;
import com.yourcompany.salesmanagement.module.importjob.service.ImportJobService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1/import-jobs", "/api/import-jobs"})
public class ImportJobController {
    private final ImportJobService importJobService;

    public ImportJobController(ImportJobService importJobService) {
        this.importJobService = importJobService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('IMPORT_JOB_READ') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<ImportJobResponse> get(@PathVariable Long id) {
        return BaseResponse.ok("Import job fetched successfully", importJobService.getById(id));
    }
}


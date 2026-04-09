package com.yourcompany.salesmanagement.module.importjob.service;

import com.yourcompany.salesmanagement.module.importjob.dto.response.ImportJobResponse;

public interface ImportJobService {
    ImportJobResponse getById(Long id);
}


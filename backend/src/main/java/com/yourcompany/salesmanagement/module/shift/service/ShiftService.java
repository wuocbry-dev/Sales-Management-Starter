package com.yourcompany.salesmanagement.module.shift.service;

import com.yourcompany.salesmanagement.module.shift.dto.request.CloseShiftRequest;
import com.yourcompany.salesmanagement.module.shift.dto.request.OpenShiftRequest;
import com.yourcompany.salesmanagement.module.shift.dto.response.ShiftResponse;
import com.yourcompany.salesmanagement.module.shift.dto.response.ShiftSummaryResponse;

public interface ShiftService {
    ShiftResponse open(OpenShiftRequest request);
    ShiftResponse close(Long shiftId, CloseShiftRequest request);
    ShiftResponse getCurrent(Long branchId);
    ShiftSummaryResponse getSummary(Long shiftId);
}


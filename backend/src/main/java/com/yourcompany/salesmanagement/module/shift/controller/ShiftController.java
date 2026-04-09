package com.yourcompany.salesmanagement.module.shift.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.shift.dto.request.CloseShiftRequest;
import com.yourcompany.salesmanagement.module.shift.dto.request.OpenShiftRequest;
import com.yourcompany.salesmanagement.module.shift.dto.response.ShiftResponse;
import com.yourcompany.salesmanagement.module.shift.dto.response.ShiftSummaryResponse;
import com.yourcompany.salesmanagement.module.shift.service.ShiftService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/shifts", "/api/shifts"})
public class ShiftController {
    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @PostMapping("/open")
    @PreAuthorize("hasAuthority('SHIFT_OPEN') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<ShiftResponse> open(@Valid @RequestBody OpenShiftRequest request) {
        return BaseResponse.ok("Shift opened successfully", shiftService.open(request));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasAuthority('SHIFT_CLOSE') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<ShiftResponse> close(@PathVariable Long id, @Valid @RequestBody CloseShiftRequest request) {
        return BaseResponse.ok("Shift closed successfully", shiftService.close(id, request));
    }

    @GetMapping("/current")
    @PreAuthorize("hasAuthority('SHIFT_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<ShiftResponse> current(@RequestParam Long branchId) {
        return BaseResponse.ok("Current shift fetched successfully", shiftService.getCurrent(branchId));
    }

    @GetMapping("/{id}/summary")
    @PreAuthorize("hasAuthority('SHIFT_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<ShiftSummaryResponse> summary(@PathVariable Long id) {
        return BaseResponse.ok("Shift summary fetched successfully", shiftService.getSummary(id));
    }
}


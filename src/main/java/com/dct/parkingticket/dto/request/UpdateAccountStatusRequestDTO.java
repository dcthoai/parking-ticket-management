package com.dct.parkingticket.dto.request;

import com.dct.parkingticket.constants.AccountConstants;
import com.dct.parkingticket.constants.ExceptionConstants;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class UpdateAccountStatusRequestDTO {

    @NotNull(message = ExceptionConstants.ID_NOT_NULL)
    @Min(value = 1, message = ExceptionConstants.ID_INVALID)
    private Integer accountId;

    @NotBlank(message = ExceptionConstants.STATUS_NOT_BLANK)
    @Pattern(regexp = AccountConstants.STATUS.PATTERN, message = ExceptionConstants.STATUS_INVALID)
    private String status;

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

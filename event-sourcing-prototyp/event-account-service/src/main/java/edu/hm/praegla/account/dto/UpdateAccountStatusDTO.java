package edu.hm.praegla.account.dto;

import edu.hm.praegla.account.entity.AccountStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateAccountStatusDTO {
    @NotNull
    private AccountStatus status;
}

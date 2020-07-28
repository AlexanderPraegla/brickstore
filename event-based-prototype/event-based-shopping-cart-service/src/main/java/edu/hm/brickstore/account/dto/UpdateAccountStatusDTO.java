package edu.hm.brickstore.account.dto;

import edu.hm.brickstore.account.entity.AccountStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateAccountStatusDTO {
    @NotNull
    private AccountStatus status;
}

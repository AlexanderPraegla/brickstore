package edu.hm.praegla.account.dto;

import edu.hm.praegla.account.entity.AccountStatus;
import lombok.Data;

@Data
public class DeactivateAccountDTO {

    private AccountStatus status = AccountStatus.DEACTIVATED;
}

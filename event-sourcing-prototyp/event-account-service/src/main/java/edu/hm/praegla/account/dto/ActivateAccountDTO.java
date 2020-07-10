package edu.hm.praegla.account.dto;

import edu.hm.praegla.account.entity.AccountStatus;
import lombok.Data;

@Data
public class ActivateAccountDTO {

    private AccountStatus status = AccountStatus.ACTIVATED;
}

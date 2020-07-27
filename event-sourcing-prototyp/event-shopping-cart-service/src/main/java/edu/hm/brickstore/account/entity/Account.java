package edu.hm.brickstore.account.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Document(collection = "account")
public class Account {

    @Id
    private String id;
    @Min(1)
    private long accountId;
    @NotNull
    private String customerName;
    @NonNull
    private AccountStatus status;
}

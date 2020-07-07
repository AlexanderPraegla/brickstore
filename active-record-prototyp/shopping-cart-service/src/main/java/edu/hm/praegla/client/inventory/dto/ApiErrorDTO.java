package edu.hm.praegla.client.inventory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiErrorDTO {

    private String status;
    private String responseCode;
    private String message;
    private List<String> errors;
}

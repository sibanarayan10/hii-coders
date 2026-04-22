package com.sibanarayan.code.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomTestCaseRequest {
    @NotBlank
    private String inputData;
}

package com.bookworm.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BeneficiaryRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 150)
    private String name;

    @Size(max = 255)
    private String description;

    private Boolean isActive;
}

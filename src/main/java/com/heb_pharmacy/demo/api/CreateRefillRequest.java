package com.heb_pharmacy.demo.api;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRefillRequest {
    @NotBlank
    private String storeCode;
} 
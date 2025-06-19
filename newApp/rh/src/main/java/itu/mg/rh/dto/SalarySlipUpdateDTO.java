package itu.mg.rh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class SalarySlipUpdateDTO {
    @NotBlank
    String salaryComponent;
    @NotBlank
    String condition;
    double percentage;
}

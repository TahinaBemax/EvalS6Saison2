package itu.mg.rh.dto;

import itu.mg.rh.models.SalaryComponent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SalaryStructureFormDto {
    @NotBlank
    @NotNull
    String name;

    @NotBlank
    @NotNull
    String company;

    @NotBlank
    @NotNull
    String isActive;

    @NotBlank
    @NotNull
    String currency;

    @NotBlank
    @NotNull
    String payrollFrequency;

    String letterHead;
    String modeOfPayment;
    String paymentAccount;
    boolean salarySlipBasedOnTimesheet;
    List<SalaryComponent> earnings;
    List<SalaryComponent> deductions;
}

package itu.mg.rh.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalaryStructure {
    @NotBlank
    @NotNull
    String name;
    @NotBlank
    @NotNull
    String company;
    @NotBlank
    @NotNull
    String isActive;

    String letterHead;
    int isDefault;

    @NotBlank
    @NotNull
    String currency;

    @NotBlank
    @NotNull
    String payrollFrequency;
    String salaryComponent;
    String modeOfPayment;
    String paymentAccount;

    double totalEarning;
    double totalDeduction;
    double netPay;
    int salarySlipBasedOnTimesheet;
    int docstatus;
}

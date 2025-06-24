package itu.mg.rh.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalaryComponent {
    @NotNull
    String name;
    @NotNull
    String salaryComponent;
    @NotNull
    String salaryComponentAbbr;
    @NotNull
    String type;
    int dependsOnPaymentDays;
    int isTaxApplicable;
    double amount;
    int amountBasedOnFormula;
    String formula;

    int docstatus;
    int idx;
    int deductFullTaxOnSelectedPayrollDate;
    int variableBasedOnTaxableSalary;
    int isIncomeTaxComponent;
    int exemptedFromIncomeTax;
    int roundToTheNearestInteger;
    int statisticalComponent;
    int doNotIncludeInTotal;
    int removeIfZeroValued;
    int disabled;
    String condition;
    String description;
    int isFlexibleBenefit;
}

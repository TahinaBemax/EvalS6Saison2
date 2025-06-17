package itu.mg.rh.models;

import lombok.Data;

@Data
public class SalaryComponent {
    String name;
    int docstatus;
    int idx;
    String salaryComponent;
    String salaryComponentAbbr;
    String type;
    int dependsOnPaymentDays;
    int isTaxApplicable;
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
    double amount;
    int amountBasedOnFormula;
    String formula;
    int isFlexibleBenefit;
}

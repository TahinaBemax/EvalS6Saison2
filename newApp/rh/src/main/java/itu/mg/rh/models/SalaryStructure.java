package itu.mg.rh.models;

import lombok.Data;

@Data
public class SalaryStructure {
    String name;
    String company;
    String isActive;
    String letterHead;
    int isDefault;
    String currency;
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

package itu.mg.rh.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PayrollEntry {
    String name;
    LocalDate postingDate;
    String company;
    String payrollFrequency;
    LocalDate startDate;
    LocalDate endDate;
    String salarySlips;
    String status;
    String paymentAccount;
    String modeOfPayment;
    int salarySlipBasedOnTimesheet;
    String payrollPayableAccount;
    String currency;
    int docstatus;
}

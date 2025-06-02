package itu.mg.rh.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class SalarySlip {
    private String name;
    private String currency;
    private String department;
    private String employeeId;
    private String employeeName;
    private String company;
    private String designation;
    private String branch;
    private String status;
    private String payrollFrequency;
    private String modeOfPayment;
    private String totalInWords;
    private Double totalWorkingDays;
    private Double absentDays;
    private Double paymentDays;
    private LocalDate paymentDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalDeduction;
    private Double totalEarnings;
    private Double netPay;
    private Double grossPay;
    private String salaryStructure;
    private String paymentStatus;
    private String payroll_frequency;

    private List<SalaryDetail> salaryDetailEarnings;
    private List<SalaryDetail> salaryDetailDeductions;
}

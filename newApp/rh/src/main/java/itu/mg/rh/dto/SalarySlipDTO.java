package itu.mg.rh.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
public class SalarySlipDTO {
    private String employeeId;
    private String employeeName;
    private String company;
    private LocalDate paymentDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal basicSalary;
    private Map<String, BigDecimal> earnings;
    private Map<String, BigDecimal> deductions;
    private BigDecimal grossSalary;
    private BigDecimal netSalary;
    private String salaryStructure;
    private String paymentStatus;
}
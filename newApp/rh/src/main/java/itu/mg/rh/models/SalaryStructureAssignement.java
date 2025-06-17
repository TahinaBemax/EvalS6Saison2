package itu.mg.rh.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SalaryStructureAssignement {
    String name;
    String employee_name;
    String employee;
    String company;
    String salary_structure;
    LocalDate from_date;
    String payroll_payable_account;
    String currency;
    int docstatus;
    double base;
    double variable;
}

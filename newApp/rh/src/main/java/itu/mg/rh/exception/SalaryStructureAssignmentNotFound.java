package itu.mg.rh.exception;

import java.time.LocalDate;

public class SalaryStructureAssignmentNotFound extends RuntimeException{
    public SalaryStructureAssignmentNotFound(String name) {
        super(String.format("Salary Structure Assignment %s Not Found", name));
    }

    public SalaryStructureAssignmentNotFound(String employee, String salaryStructure, LocalDate endDate) {
        super(String.format("Salary Structure Assignment For Employee %s, Salary Structure %s before the date %s is Not Found", employee, salaryStructure, endDate.toString()));
    }
}

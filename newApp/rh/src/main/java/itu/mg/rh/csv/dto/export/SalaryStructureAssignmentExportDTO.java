package itu.mg.rh.csv.dto.export;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SalaryStructureAssignmentExportDTO {
    @CsvBindByName(column = "ID", required = true)
    String id;

    @CsvBindByName(column = "Employee", required = true)
    String employee;

    @CsvBindByName(column = "Salary Structure", required = true)
    String salaryStructure;

    @CsvBindByName(column = "From Date", required = true)
    LocalDate fromDate;

    @CsvBindByName(column = "Company", required = true)
    String company;

    @CsvBindByName(column = "Currency", required = true)
    String currency;

    @CsvBindByName(column = "Employee Name", required = true)
    String employeeName;

    @CsvBindByName(column = "Base", required = true)
    Double Base;

    @CsvBindByName(column = "Payroll Payable Account")
    String payrollPayableAccount;
}

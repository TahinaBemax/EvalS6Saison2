package itu.mg.rh.csv.dto.export;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SalarySlipExportDTO {
    @CsvBindByName(column = "ID", required = true)
    String id;

    @CsvBindByName(column = "Employee", required = true)
    String employee;

    @CsvBindByName(column = "Employee Name", required = true)
    String employeeName;

    @CsvBindByName(column = "Company", required = true)
    String company;

    @CsvBindByName(column = "Posting Date", required = true)
    LocalDate postingDate;

/*    @CsvBindByName(column = "Start Date", required = true)
    LocalDate startDate;

    @CsvBindByName(column = "End Date", required = true)
    LocalDate EndDate;

    */
    @CsvBindByName(column = "Mode Of Payment", required = true)
    String ModeOfPayment;
    @CsvBindByName(column = "Currency", required = true)
    String currency;

    @CsvBindByName(column = "Exchange Rate", required = true)
    Double exchangeRate;

    @CsvBindByName(column = "Salary Structure", required = true)
    String salaryStructure;

    @CsvBindByName(column = "Working Days")
    int workingDays;

    @CsvBindByName(column = "Payment Days")
    int paymentDays;

    @CsvBindByName(column = "Payroll Frequency", required = true)
    String PayrollFrequency;

    @CsvBindByName(column = "status", required = true)
    String status;

    @CsvBindByName(column = "ID (Earnings)", required = true)
    String idEarnings;

    @CsvBindByName(column = "Component (Earnings)", required = true)
    String componentEarnings;

    @CsvBindByName(column = "ID (Deductions)", required = true)
    String idDeductions;

    @CsvBindByName(column = "Component (Deductions)", required = true)
    String componentDeductions;


    public void setWorkingDays() {
        this.workingDays = this.postingDate.lengthOfMonth();
    }

    public void setPaymentDays() {
        this.paymentDays = this.postingDate.lengthOfMonth();
    }
}

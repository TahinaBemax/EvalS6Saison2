package itu.mg.rh.csv.dto.export;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class SalaryStructureExportDTO {
    @CsvBindByName(column = "ID", required = true)
    String id;

    @CsvBindByName(column = "Company", required = true)
    String company;

    @CsvBindByName(column = "Is Active", required = true)
    String isActive;

    @CsvBindByName(column = "Currency", required = true)
    String currency;

    @CsvBindByName(column = "Payroll Frequency", required = true)
    String payrollFrequency;

    @CsvBindByName(column = "Salary Component", required = true)
    String salaryComponent;

    @CsvBindByName(column = "Mode of Payment", required = true)
    String modeOfPayment;

    @CsvBindByName(column = "Payment Account", required = true)
    String paymentAccount;


    @CsvBindByName(column = "ID (Earnings)")
    String idEarnings;

    @CsvBindByName(column = "Component (Earnings)")
    String componentEarnings;

    @CsvBindByName(column = "Formula (Earnings)")
    String formulaEarnings;

    @CsvBindByName(column = "Amount based on formula (Earnings)")
    Integer amountBasedOnFormulaEarnings;

    @CsvBindByName(column = "Abbr (Earnings)")
    String abbrEarnings;

    @CsvBindByName(column = "Depends on Payment Days (Earnings)")
    Integer dependsOnPaymentDaysEarnings;

    @CsvBindByName(column = "Is Tax Applicable (Earnings)")
    Integer isTaxApplicableEarnings;


    @CsvBindByName(column = "ID (Deductions)")
    String idDeductions;

    @CsvBindByName(column = "Component (Deductions)")
    String componentDeductions;

    @CsvBindByName(column = "Amount based on formula (Deductions)")
    Integer AmountBasedOnFormulaDeductions;

    @CsvBindByName(column = "Formula (Deductions)")
    String FormulaDeductions;

    @CsvBindByName(column = "Abbr (Deductions)")
    String abbrDeductions;

    @CsvBindByName(column = "Depends on Payment Days (Deductions)")
    Integer dependsOnPaymentDaysDeductions;

    @CsvBindByName(column = "Is Tax Applicable (Deductions)")
    Integer isTaxApplicableDeductions;
}

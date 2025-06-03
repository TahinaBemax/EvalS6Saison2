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

    @CsvBindByName(column = "Mode of Payment", required = true)
    String modeOfPayment;

    @CsvBindByName(column = "Payment Account", required = true)
    String paymentAccount;


    @CsvBindByName(column = "ID (Earnings)")
    String iDEarnings;

    @CsvBindByName(column = "Component (Earnings)")
    String componentEarnings;

    @CsvBindByName(column = "Formula (Earnings)")
    String formulaEarnings;

    @CsvBindByName(column = "Amount based on formula (Earnings)")
    String AmountBasedOnFormulaEarnings;

    @CsvBindByName(column = "ID (Deductions)")
    String idDeductions;

    @CsvBindByName(column = "Component (Deductions)")
    String componentDeductions;

    @CsvBindByName(column = "Amount based on formula (Deductions)")
    String AmountBasedOnFormulaDeductions;

    @CsvBindByName(column = "Formula (Deductions)")
    String FormulaDeductions;
}

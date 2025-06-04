package itu.mg.rh.csv.dto.export;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class SalaryComponentExportDTO {
    @CsvBindByName(column = "Name", required = true)
    String name;

    @CsvBindByName(column = "Abbr", required = true)
    String abbr;

    @CsvBindByName(column = "Type", required = true)
    String type;

    @CsvBindByName(column = "Amount based on formula", required = true)
    Integer AmountBasedOnFormula;

    @CsvBindByName(column = "Formula", required = true)
    String formula;

    @CsvBindByName(column = "Depends on Payment Days")
    Integer dependsOnPaymentDays;

    @CsvBindByName(column = "Remove if Zero Valued")
    Integer removeIfZeroValued;

    @CsvBindByName(column = "ID (Accounts)", required = true)
    String idAccounts;

    @CsvBindByName(column = "Company (Accounts)", required = true)
    String companyAccounts;

    @CsvBindByName(column = "Account (Accounts)", required = true)
    String accountAccounts;
}

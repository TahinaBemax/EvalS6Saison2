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

    @CsvBindByName(column = "Formula", required = true)
    String formula;

    @CsvBindByName(column = "Amount based on formula", required = true)
    String AmountBasedOnFormula;

    @CsvBindByName(column = "ID (Accounts)", required = true)
    String idAccounts;

    @CsvBindByName(column = "Company (Accounts)", required = true)
    String companyAccounts;

    @CsvBindByName(column = "Account (Accounts)", required = true)
    String accountAccounts;
}

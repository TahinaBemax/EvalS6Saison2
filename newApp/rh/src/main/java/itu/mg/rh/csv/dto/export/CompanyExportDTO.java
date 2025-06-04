package itu.mg.rh.csv.dto.export;


import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import itu.mg.rh.csv.customValidator.UniqueEmployeeFullNameConverter;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CompanyExportDTO {
    @CsvBindByName(column = "Company", required = true)
    String company;

    @CsvBindByName(column = "Abbr", required = true)
    String abbr;

    @CsvBindByName(column = "Default Currency", required = true)
    String defaultCurrency;

    @CsvBindByName(column = "Country")
    String country;
    @CsvCustomBindByName(column = "Default Holidays List", converter = UniqueEmployeeFullNameConverter.class)
    String defaultHolidaysList;
    public CompanyExportDTO(String company) {
        this.company = company;
        setAbbr();
        this.defaultCurrency = "EUR";
        this.country = "Madagascar";
        this.defaultHolidaysList = "Jour Ferié";
    }
    private void setAbbr(){
        this.abbr = getAbbr(this.company);
    }

    public static String getAbbr(String company){
        String[] words = company.split("\\s+");

        StringBuilder initials = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()){
                initials.append(word.substring(0, 1).toUpperCase());
            }
        }

        return initials.toString();
    }
}
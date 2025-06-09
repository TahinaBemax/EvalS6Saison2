package itu.mg.rh.csv.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import itu.mg.rh.csv.customValidator.Required;
import itu.mg.rh.csv.customValidator.StructureComponentTypeConverter;
import itu.mg.rh.csv.customValidator.UniqueSalaryComponentAbbr;
import itu.mg.rh.csv.customValidator.UniqueSalaryComponentName;
import lombok.Data;

@Data
public class SalaryComponentDTO {

    @CsvCustomBindByName(column = "salary structure", converter = Required.class, required = true)
    String salaryStructure;

    @CsvBindByName(column = "name", required = true)
    String name;

    @CsvBindByName(column = "Abbr", required = true)
    String abbr;

    @CsvCustomBindByName(column = "type", converter = StructureComponentTypeConverter.class, required = true)
    String type;

    @CsvBindByName(column = "valeur", required = true)
    String valeur;

    @CsvBindByName(column = "company", required = true)
    String company;
}

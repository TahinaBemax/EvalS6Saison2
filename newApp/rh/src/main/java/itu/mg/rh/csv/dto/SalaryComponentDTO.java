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

    @CsvCustomBindByName(column = "name", converter = UniqueSalaryComponentName.class,required = true)
    String name;

    @CsvCustomBindByName(column = "Abbr", converter = UniqueSalaryComponentAbbr.class, required = true)
    String abbr;

    @CsvCustomBindByName(column = "type", converter = StructureComponentTypeConverter.class, required = true)
    String type;

    @CsvBindByName(column = "valeur", required = true)
    String valeur;

    @CsvBindByName(column = "company", required = true)
    String company;
}

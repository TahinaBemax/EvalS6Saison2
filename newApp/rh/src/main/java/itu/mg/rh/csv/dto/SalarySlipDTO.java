package itu.mg.rh.csv.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvNumber;
import itu.mg.rh.csv.customValidator.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SalarySlipDTO {

    @CsvCustomBindByName(column = "Mois", converter = LocalDateConverter.class, required = true)
    LocalDate mois;

    @CsvBindByName(column = "Ref Employe",required = true)
    String refEmployee;

    @CsvCustomBindByName(column = "Salaire Base", converter = AmountConverter.class  ,required = true)
    Double baseSalary;

    @CsvBindByName(column = "salaire", required = true)
    String salary;
}

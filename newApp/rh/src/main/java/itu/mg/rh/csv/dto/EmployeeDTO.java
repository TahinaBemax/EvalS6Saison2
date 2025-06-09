package itu.mg.rh.csv.dto;


import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import itu.mg.rh.csv.customValidator.GenderConverter;
import itu.mg.rh.csv.customValidator.Required;
import itu.mg.rh.csv.customValidator.LocalDateConverter;
import itu.mg.rh.csv.customValidator.UniqueEmployeeRef;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeDTO {
    @CsvCustomBindByName(column = "Ref", converter = UniqueEmployeeRef.class, required = true)
    String ref;

    @CsvCustomBindByName(column = "Nom", converter = Required.class, required = true)
    String firstName;

    @CsvBindByName(column = "Prenom")
    String lastName;

    @CsvCustomBindByName(column = "genre", converter = GenderConverter.class, required = true)
    String gender;

    @CsvCustomBindByName(column = "Date embauche", converter = LocalDateConverter.class, required = true)
    @CsvDate
    LocalDate hireDate;

    @CsvCustomBindByName(column = "date naissance", converter = LocalDateConverter.class, required = true)
    @CsvDate
    LocalDate dateOfBirth;

    @CsvCustomBindByName(column = "company", converter = Required.class, required = true)
    String company;
}
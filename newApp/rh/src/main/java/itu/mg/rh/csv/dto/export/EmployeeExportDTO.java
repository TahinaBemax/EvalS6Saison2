package itu.mg.rh.csv.dto.export;


import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import itu.mg.rh.csv.customValidator.LocalDateConverter;
import itu.mg.rh.csv.customValidator.Required;
import itu.mg.rh.csv.customValidator.UniqueEmployeeFullNameConverter;
import itu.mg.rh.csv.customValidator.UniqueEmployeeRef;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeExportDTO {
    @CsvBindByName(column = "ID", required = true)
    String id;

    @CsvBindByName(column = "Series")
    String series;

    @CsvBindByName(column = "First Name", required = true)
    String firstName;

    @CsvBindByName(column = "Last Name")
    String lastName;

    @CsvCustomBindByName(column = "Full Name", converter = UniqueEmployeeFullNameConverter.class)
    String fullName;

    @CsvBindByName(column = "Gender", required = true)
    String gender;

    @CsvBindByName(column = "Date of Joining", required = true)
    LocalDate hireDate;

    @CsvBindByName(column = "Date of Birth", required = true)
    LocalDate dateOfBirth;

    @CsvBindByName(column = "Status", required = true)
    String status;

    @CsvBindByName(column = "Company", required = true)
    String company;

    @CsvBindByName(column = "Holiday List", required = true)
    String holidayList;

    @CsvBindByName(column = "Salary Currency", required = true)
    String salaryCurrency;

    public void setFullName(){
        this.fullName = lastName != null ? firstName + " " + lastName: firstName;
    }

    public void setID(){

    }
}
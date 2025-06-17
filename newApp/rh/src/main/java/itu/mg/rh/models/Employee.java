package itu.mg.rh.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Employee {
    String firstName;
    String lastName;
    String branch;
    @NotNull
    String fullName;
    @NotNull
    String employeID;
    String status;
    @NotNull
    String company;
    @NotNull
    String gender;
    @NotNull
    LocalDate dateOfBirth;
    String department;
    String designation;
    String employmentType;
    String grade;

    String reportsTo;
    String holidayList;
    LocalDate dateOfJoining;
}

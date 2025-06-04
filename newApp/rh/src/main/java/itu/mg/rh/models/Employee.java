package itu.mg.rh.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Employee {
    @NotNull
    String fullName;
    @NotNull
    String employeID;
    @NotNull
    String status;
    @NotNull
    String company;
    @NotNull
    String gender;
    @NotNull
    LocalDate dateOfBirth;
    @NotNull
    String department;
    @NotNull
    String designation;
    @NotNull
    String employementType;
    @NotNull
    String grade;

    String reportsTo;
    LocalDate dateOfJoining;
}

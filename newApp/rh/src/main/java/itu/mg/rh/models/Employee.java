package itu.mg.rh.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Employee {
    String employeeName;
    String employeID;
    String Status;
    String company;
    LocalDate dateOfJoining;
    String gender;
    LocalDate dateOfBirth;
    String departement;
    String designation;
    String employementType;
    String reportsTo;
}

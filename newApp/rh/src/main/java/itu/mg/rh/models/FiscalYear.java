package itu.mg.rh.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FiscalYear {
    private String name;
    private String year;
    private LocalDate yearStartDate;
    private LocalDate yearEndDate;
    private Integer isShortYear;
    private String company;
    private Integer isClosed;
} 
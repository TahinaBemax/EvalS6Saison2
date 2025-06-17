package itu.mg.rh.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SalaryDTO {
    List<String> employeeName;
    Double base;
    LocalDate startDate;
    LocalDate endDate;
}

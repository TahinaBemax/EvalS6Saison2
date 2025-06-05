package itu.mg.rh.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TotalSalarySlipPerMonth {
    int month;
    int year;
    double totalAmount;
    List<Map<String, Object>> details;
}

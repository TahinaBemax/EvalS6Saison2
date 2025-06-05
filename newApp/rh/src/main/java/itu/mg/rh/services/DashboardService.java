package itu.mg.rh.services;

import itu.mg.rh.dto.TotalSalarySlipPerMonth;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DashboardService {
    List<TotalSalarySlipPerMonth> findTotalSalarySlipPerMonth(Integer year);
}

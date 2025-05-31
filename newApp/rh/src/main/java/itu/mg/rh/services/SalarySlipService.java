package itu.mg.rh.services;

import itu.mg.rh.dto.SalarySlipDTO;

import java.time.LocalDate;
import java.util.List;

public interface SalarySlipService {
    /**
     * Retrieve a specific salary slip by ID
     * @param salarySlipId The unique identifier of the salary slip
     * @return The salary slip details
     */
    SalarySlipDTO getSalarySlipById(String salarySlipId);
    
    /**
     * Get all salary slips for a specific employee
     * @param employeeId The employee's ID
     * @return List of salary slips
     */
    List<SalarySlipDTO> getEmployeeSalarySlips(String employeeId);
    
    /**
     * Get salary slip for an employee for a specific period
     * @param employeeId The employee's ID
     * @param startDate Start date of the period
     * @param endDate End date of the period
     * @return The salary slip for the specified period
     */
    SalarySlipDTO getEmployeeSalarySlipForPeriod(String employeeId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Generate a new salary slip for an employee
     * @param employeeId The employee's ID
     * @param periodStartDate Start date of the salary period
     * @param periodEndDate End date of the salary period
     * @return The generated salary slip
     */
    SalarySlipDTO generateSalarySlip(String employeeId, LocalDate periodStartDate, LocalDate periodEndDate);
}
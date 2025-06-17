package itu.mg.rh.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import itu.mg.rh.models.SalaryDetail;
import itu.mg.rh.models.SalarySlip;

import java.time.LocalDate;
import java.util.List;

public interface SalarySlipService {
    /**
     * Get all salary Detail for a specific salary slip
     * @param salarySlipName
     * @return
     */
    public List<SalaryDetail> getSalaryDetail(String salarySlipName);

    /**
     * Retrieve a specific salary slip by ID
     * @param salarySlipId The unique identifier of the salary slip
     * @return The salary slip details
     */
    SalarySlip getSalarySlipById(String salarySlipId);
    
    /**
     * Get all salary slips for a specific employee
     * @param employeeId The employee's ID
     * @return List of salary slips
     */
    List<SalarySlip> getEmployeeSalarySlips(String employeeId) throws JsonProcessingException;

    /**
     * Get Salary Slip by Month
     * @Param int Month
     * @return List of salary Slips
     */
    List<SalarySlip> getSalarySlipsByMonth(Integer month) throws JsonProcessingException;


    /**
     * Get all salary slips for all employee
     * @return List of salary slips
     */
    List<SalarySlip> getSalarySlips() throws JsonProcessingException;

    /**
     * Get all salary slips with salary detail for all employee for a given month and year
     * @return List of salary slips
     */
    List<SalarySlip> findSalaryEmployeeDetails(Integer month, Integer year) throws JsonProcessingException;

    /**
     * Export a salary Slip for a specific user to pdf
     * @return byte[]
     */
     byte[] exportSalarySlipToPdf(String salarySlipId);
    /**
     * Get salary slip for an employee for a specific period
     * @param employeeId The employee's ID
     * @param startDate Start date of the period
     * @param endDate End date of the period
     * @return The salary slip for the specified period
     */
    SalarySlip getEmployeeSalarySlipForPeriod(String employeeId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Generate a new salary slip for an employee
     * @param employeeId The employee's ID
     * @param periodStartDate Start date of the salary period
     * @param periodEndDate End date of the salary period
     * @return The generated salary slip
     */
    SalarySlip generateSalarySlip(String employeeId, LocalDate periodStartDate, LocalDate periodEndDate);

    List<SalarySlip> getSalarySlip(String employee, String payrollEntry);
    List<SalarySlip> findAll();
    boolean save(SalarySlip salarySlip);
    boolean delete(String name);
    SalarySlip findById(String name);
    boolean update(SalarySlip salarySlip);
}
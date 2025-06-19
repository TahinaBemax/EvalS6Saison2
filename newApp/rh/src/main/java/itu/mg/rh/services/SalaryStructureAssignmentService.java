package itu.mg.rh.services;

import itu.mg.rh.csv.dto.export.SalaryStructureAssignmentExportDTO;
import itu.mg.rh.dto.SalaryDTO;
import itu.mg.rh.exception.FrappeApiException;
import itu.mg.rh.models.SalaryStructureAssignement;

import java.time.LocalDate;
import java.util.List;

public interface SalaryStructureAssignmentService {
    /**
     * Find a Salary Structure Assignment for a specific employee and salary structure before the endDate. And Docstatus = 1 (active)
     * @param employee Employee ID
     * @param salaryStructure Salary Structure Name
     * @param endDate End Date
     * @return Salary Structure Assignment or null
     */

    SalaryStructureAssignement findBySalarySlipIdAndEndDate(String employee, String salaryStructure,LocalDate endDate);
    /**
     * Insert a list of salary structures into the Frappe RH module
     * @param salaryStructureAssignments List of salary structures assignment to insert
     * @return Response indicating success or failure
     */
    boolean saveSalaryStructureAssignment(List<SalaryStructureAssignmentExportDTO> salaryStructureAssignments);

    /**
     * Insert a salary structures assignment into the Frappe RH module
     * @param salaryStructureAssignment salary structures assignment to insert
     * @return Response indicating success or failure
     */
    boolean save(SalaryStructureAssignement salaryStructureAssignment) throws FrappeApiException;

    /**
     * Insert
     * @param salaryDTO salary  assignment to insert
     * @return Response indicating success or failure
     */
    boolean saveAll(SalaryDTO salaryDTO) throws FrappeApiException;

    /**
     * This function cancel the existing Salary Structure Assignment and create new one
     * @param salaryStructureAssignement the salary structure assignment to update
     * @return Response indicating success or failure
     */
    boolean update(SalaryStructureAssignement salaryStructureAssignement);

    /**
     * This function delete a Salary Structure Assignment
     * @param name the salary structure assignment name to delete
     * @return Response indicating success or failure
     */
    boolean delete(String name);

    /**
     * This function cancel the existing Salary Structure Assignment
     * @param name the salary structure assignment name to cancel
     * @return Response indicating success or failure
     */
    boolean cancel(String name);

    /**
     * Find all salary structure assignment for a specific employee
     * @return List of Salary Structure Assignment for a specific employee
     */
    List<SalaryStructureAssignement> findAllByEmployeeID(String employee);

    /**
     * Find all salary structure assignment
     * @return List of Salary Structure Assignment
     */
    List<SalaryStructureAssignement> findAll();
} 
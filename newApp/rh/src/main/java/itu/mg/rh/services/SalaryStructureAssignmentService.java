package itu.mg.rh.services;

import itu.mg.rh.csv.dto.export.SalaryStructureAssignmentExportDTO;
import itu.mg.rh.dto.SalaryDTO;
import itu.mg.rh.models.SalaryStructureAssignement;

import java.util.List;

public interface SalaryStructureAssignmentService {
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
    boolean save(SalaryStructureAssignement salaryStructureAssignment);

    /**
     * Insert
     * @param salaryDTO salary  assignment to insert
     * @return Response indicating success or failure
     */
    boolean saveAll(SalaryDTO salaryDTO);

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
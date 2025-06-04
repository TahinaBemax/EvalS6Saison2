package itu.mg.rh.services;

import itu.mg.rh.csv.dto.export.SalaryStructureAssignmentExportDTO;
import itu.mg.rh.csv.dto.export.SalaryStructureExportDTO;

import java.util.List;

public interface SalaryStructureAssignmentService {
    /**
     * Insert a list of salary structures into the Frappe RH module
     * @param salaryStructureAssignments List of salary structures assignment to insert
     * @return Response indicating success or failure
     */
    boolean saveSalaryStructureAssignment(List<SalaryStructureAssignmentExportDTO> salaryStructureAssignments);
} 
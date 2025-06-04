package itu.mg.rh.services;

import itu.mg.rh.csv.dto.export.SalaryStructureExportDTO;
import java.util.List;

public interface SalaryStructureService {
    /**
     * Insert a list of salary structures into the Frappe RH module
     * @param salaryStructures List of salary structures to insert
     * @return Response indicating success or failure
     */
    boolean insertSalaryStructures(List<SalaryStructureExportDTO> salaryStructures);
} 
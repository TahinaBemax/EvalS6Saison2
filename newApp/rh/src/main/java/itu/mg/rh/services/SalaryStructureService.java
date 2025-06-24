package itu.mg.rh.services;

import itu.mg.rh.csv.dto.export.SalaryStructureExportDTO;
import itu.mg.rh.dto.SalaryStructureFormDto;
import itu.mg.rh.models.SalaryStructure;

import java.util.List;

public interface SalaryStructureService {
    /**
     * Insert a list of salary structures into the Frappe RH module
     * @param salaryStructures List of salary structures to insert
     * @return Response indicating success or failure
     */
    boolean insertSalaryStructures(List<SalaryStructureExportDTO> salaryStructures);

    List<SalaryStructure> findAllActiveSalaryStructure();

    List<SalaryStructure> getSalaryStructure(String name, String company);

    List<SalaryStructure> findAll();

    boolean save(SalaryStructure salaryStructure);
    boolean save(SalaryStructureFormDto salaryStructure);

    boolean delete(String name);

    SalaryStructure findById(String name);

    boolean update(SalaryStructure salaryStructure);
} 
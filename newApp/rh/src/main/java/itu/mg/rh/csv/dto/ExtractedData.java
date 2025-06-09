package itu.mg.rh.csv.dto;

import itu.mg.rh.csv.dto.export.*;
import lombok.Data;

import java.util.List;

@Data
public class ExtractedData {
    List<CompanyExportDTO> company;
    List<EmployeeExportDTO> employees;
    List<SalaryComponentExportDTO> salaryComponent;
    List<SalaryStructureExportDTO> salaryStructures;
    List<SalaryStructureAssignmentExportDTO> salaryAssigments;
    List<SalarySlipExportDTO> salarySlips;

    public ExtractedData(List<CompanyExportDTO> company, List<EmployeeExportDTO> employees, List<SalaryComponentExportDTO> salaryComponent, List<SalaryStructureExportDTO> salaryStructures, List<SalaryStructureAssignmentExportDTO> salaryAssigments, List<SalarySlipExportDTO> salarySlips) {
        this.company = company;
        this.employees = employees;
        this.salaryComponent = salaryComponent;
        this.salaryStructures = salaryStructures;
        this.setSalaryAssigments(salaryAssigments);
        this.setSalarySlips(salarySlips);
    }

    public void setSalaryAssigments(List<SalaryStructureAssignmentExportDTO> salaryAssigments) {
        this.salaryAssigments = salaryAssigments;
        this.setSalaryStructureAssignmentEmployeeID();
    }

    public void setSalarySlips(List<SalarySlipExportDTO> salarySlips) {
        this.salarySlips = salarySlips;
        this.setSalarySlipEmployeeID();
    }


    private void setSalaryStructureAssignmentEmployeeID(){
        for (SalaryStructureAssignmentExportDTO dto : this.salaryAssigments) {
            for (EmployeeExportDTO employee : this.employees) {
                if (dto.getEmployeeName().equals(employee.getFullName())){
                    dto.setEmployee(employee.getId());
                    break;
                }
            }
        }
    }
    private void setSalarySlipEmployeeID(){
        for (SalarySlipExportDTO dto : this.salarySlips) {
            for (EmployeeExportDTO employee : this.employees) {
                if (dto.getEmployeeName().equals(employee.getFullName())){
                    dto.setEmployee(employee.getId());
                    break;
                }
            }
        }
    }
}

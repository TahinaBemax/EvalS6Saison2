package itu.mg.rh.services.helper;

import itu.mg.rh.csv.dto.export.SalaryStructureAssignmentExportDTO;
import itu.mg.rh.csv.dto.export.SalaryStructureExportDTO;

import java.util.*;

public class ApiDataPreparator {

    public static List<Map<String, Object>>  salaryStructureAssignmentsData(List<SalaryStructureAssignmentExportDTO> assignments){
        // Prepare the data for Frappe API
        List<Map<String, Object>> formattedAssignments = new ArrayList<>();
        for (SalaryStructureAssignmentExportDTO assignment : assignments) {
            Map<String, Object> formattedAssignment = new HashMap<>();
            formattedAssignment.put("employee", assignment.getEmployee());
            formattedAssignment.put("employee_name", assignment.getEmployeeName());
            formattedAssignment.put("salary_structure", assignment.getSalaryStructure());
            formattedAssignment.put("from_date", assignment.getFromDate().toString());
            formattedAssignment.put("company", assignment.getCompany());
            formattedAssignment.put("currency", assignment.getCurrency());
            formattedAssignment.put("base", assignment.getBase());
            formattedAssignment.put("payroll_payable_account", assignment.getPayrollPayableAccount());
            formattedAssignments.add(formattedAssignment);
        }

        return formattedAssignments;
    }

    public static List<Map<String, Object>> salaryStructure(List<SalaryStructureExportDTO> salaryStructures){
        // Convert salary structures to the format expected by Frappe
        List<Map<String, Object>> formattedStructures = new ArrayList<>();
        for (SalaryStructureExportDTO structure : salaryStructures) {
            Map<String, Object> formattedStructure = new HashMap<>();
            formattedStructure.put("name", structure.getId());
            formattedStructure.put("company", structure.getCompany());
            formattedStructure.put("is_active", structure.getIsActive());
            formattedStructure.put("payroll_frequency", structure.getPayrollFrequency());
            formattedStructure.put("currency", structure.getCurrency());
            formattedStructure.put("mode_of_payment", structure.getModeOfPayment());
            formattedStructure.put("payment_account", structure.getPaymentAccount());
            formattedStructure.put("salary_component", structure.getSalaryComponent());

            setSalaryDetail(salaryStructures, formattedStructure);
            formattedStructures.add(formattedStructure);
        }

        return formattedStructures;
    }

    private static void setSalaryDetail(List<SalaryStructureExportDTO> salaryStructures, Map<String, Object> formattedStructure){
        List<Map<String, Object>> earnings = new ArrayList<>();
        List<Map<String, Object>> deductions = new ArrayList<>();
        Set<String> existingEarning = new HashSet<>();
        Set<String> existingDeductions = new HashSet<>();

        for (SalaryStructureExportDTO structure : salaryStructures) {
            if (formattedStructure.get("name").equals(structure.getId())){
                // Handle earnings
                if (structure.getComponentEarnings() != null && !existingEarning.contains(structure.getComponentEarnings())) {
                    existingEarning.add(structure.getComponentEarnings());

                    Map<String, Object> earning = new HashMap<>();
                    earning.put("doctype", "Salary Detail");
                    earning.put("name", structure.getIdEarnings());
                    earning.put("salary_component", structure.getComponentEarnings());
                    earning.put("formula", structure.getFormulaEarnings());
                    earning.put("amount_based_on_formula", structure.getAmountBasedOnFormulaEarnings());
                    earning.put("abbr", structure.getAbbrEarnings());
                    earning.put("depends_on_payment_days", structure.getDependsOnPaymentDaysEarnings());
                    earning.put("is_tax_applicable", structure.getIsTaxApplicableEarnings());
                    earnings.add(earning);
                }

                // Handle deductions
                if (structure.getComponentDeductions() != null && !existingDeductions.contains(structure.getComponentDeductions())) {
                    existingDeductions.add(structure.getComponentDeductions());

                    Map<String, Object> deduction = new HashMap<>();
                    deduction.put("doctype", "Salary Detail");
                    deduction.put("name", structure.getIdDeductions());
                    deduction.put("salary_component", structure.getComponentDeductions());
                    deduction.put("formula", structure.getFormulaDeductions());
                    deduction.put("amount_based_on_formula", structure.getAmountBasedOnFormulaDeductions());
                    deduction.put("abbr", structure.getAbbrDeductions());
                    deduction.put("depends_on_payment_days", structure.getDependsOnPaymentDaysDeductions());
                    deduction.put("is_tax_applicable", structure.getIsTaxApplicableDeductions());
                    deductions.add(deduction);
                }

                formattedStructure.put("earnings", earnings);
                formattedStructure.put("deductions", deductions);
            }
        }
    }
}

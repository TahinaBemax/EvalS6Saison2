package itu.mg.rh.services.helper;

import itu.mg.rh.models.SalaryDetail;
import itu.mg.rh.models.SalarySlip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalarySlipHelper {

    public static Map<String, Object> bodyPreparator(SalarySlip salarySlip){
        Map<String, Object> body = new HashMap<>();
        body.put("employee", salarySlip.getEmployeeId());
        body.put("employee_name", salarySlip.getEmployeeName());
        body.put("salary_structure", salarySlip.getSalaryStructure());
        body.put("start_date", salarySlip.getStartDate());
        body.put("end_date", salarySlip.getEndDate());
        body.put("posting_date", salarySlip.getPaymentDate());
        body.put("gross_pay", null);
        body.put("payroll_frequency", salarySlip.getPayroll_frequency());
        body.put("mode_of_payment", salarySlip.getModeOfPayment());
        body.put("earnings", earningsPreparator(salarySlip));
        body.put("deductions", deductionsPreparator(salarySlip));
        body.put("docstatus", 1);

        return body;
    }

    private static List<Map<String, Object>> earningsPreparator(SalarySlip salarySlip){
        List<Map<String, Object>> earnings = new ArrayList<>();
        if (salarySlip.getSalaryDetailEarnings() == null || salarySlip.getSalaryDetailEarnings().isEmpty())
            return earnings;

        for (SalaryDetail salaryDetailEarning : salarySlip.getSalaryDetailEarnings()) {
            Map<String, Object> earning = new HashMap<>();
            earning.put("salary_component", salaryDetailEarning.getSalaryComponent());
            earning.put("formula", salaryDetailEarning.getFormula());
            earning.put("condition", salaryDetailEarning.getCondition());
            earning.put("amount_based_on_formula", salaryDetailEarning.getAmountBasedOnFormula());
            earning.put("amount", null);

            earnings.add(earning);
        }
        return earnings;
    }

    private static List<Map<String, Object>> deductionsPreparator(SalarySlip salarySlip){
        List<Map<String, Object>> deductions = new ArrayList<>();
        if (salarySlip.getSalaryDetailDeductions() == null || salarySlip.getSalaryDetailDeductions().isEmpty())
            return deductions;

        for (SalaryDetail detailDeductions : salarySlip.getSalaryDetailDeductions()) {
            Map<String, Object> earning = new HashMap<>();
            earning.put("salary_component", detailDeductions.getSalaryComponent());
            earning.put("amount", null);
            earning.put("formula", detailDeductions.getFormula());
            earning.put("condition", detailDeductions.getCondition());
            earning.put("amount_based_on_formula", detailDeductions.getAmountBasedOnFormula());

            deductions.add(earning);
        }

        return deductions;
    }

    public static double getNewBase(double base, double percentage){
        return base + ((percentage * base) / 100.00);
    }

    public static List<SalarySlip> getMatched(String component, String condition, List<SalarySlip> salarySlips){
        double amount = extractAmount(condition);
        String critere = extractCriteria(condition);

        switch (critere.trim()){
            case ">" -> {
                return filterByComponentSuperiorAmount(component, amount, salarySlips);
            }
            case "<" -> {
                return filterByComponentInferiorAmount(component, amount, salarySlips);
            }
            default -> {
                throw new RuntimeException("Unrecognized criteria! Should < or >");
            }
        }
    }

    private static double extractAmount(String condition){
        double parsedAmount = 0;
        condition = condition.replaceAll("\\s", "");
        int idx = condition.indexOf(">");
        idx = (idx == -1) ?  condition.indexOf("<") : idx;

        if (idx == -1)
            throw new RuntimeException("Condition invalid");

        String amount = condition.substring(idx + 1);
        try {
            parsedAmount = Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            throw new RuntimeException(String.format("Failed to parse %s to double", amount), e);
        }
        return parsedAmount;
    }

    private static String extractCriteria(String condition){
        condition = condition.replaceAll("\\s", "");
        int idx = condition.indexOf(">");
        idx = (idx == -1) ?  condition.indexOf("<") : idx;

        if (idx == -1)
            throw new RuntimeException("Condition invalid");

        return String.valueOf(condition.charAt(idx));
    }

    private static List<SalarySlip> filterByComponentSuperiorAmount(String component, double amount, List<SalarySlip> slips) {
        return slips.stream()
                .filter(salarySlip -> {
                    List<SalaryDetail> earnings = salarySlip.getSalaryDetailEarnings()
                            .stream()
                            .filter(detail -> detail.getSalaryComponent().equals(component) && detail.getAmount() > amount)
                            .toList();

                    if (earnings.isEmpty()){
                        List<SalaryDetail> deductions = salarySlip.getSalaryDetailDeductions()
                                .stream()
                                .filter(detail -> detail.getSalaryComponent().equals(component) && detail.getAmount() > amount)
                                .toList();
                        return !deductions.isEmpty();
                    }

                    return true;
                }).toList();
    }

    private static List<SalarySlip> filterByComponentInferiorAmount(String component, double amount, List<SalarySlip> slips) {
        return slips.stream()
                .filter(salarySlip -> {
                    List<SalaryDetail> earnings = salarySlip.getSalaryDetailEarnings()
                            .stream()
                            .filter(detail -> detail.getSalaryComponent().equals(component) && detail.getAmount() < amount)
                            .toList();

                    if (earnings.isEmpty()){
                        List<SalaryDetail> deductions = salarySlip.getSalaryDetailDeductions()
                                .stream()
                                .filter(detail -> detail.getSalaryComponent().equals(component) && detail.getAmount() < amount)
                                .toList();
                        return !deductions.isEmpty();
                    }

                    return true;
                }).toList();
    }
}

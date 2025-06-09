package itu.mg.rh.services;

import itu.mg.rh.models.FiscalYear;
import java.time.LocalDate;
import java.util.List;

public interface FiscalYearService {
    /**
     * Get all fiscal years from Frappe
     * @return List of all fiscal years
     */
    List<FiscalYear> getAllFiscalYears();

    /**
     * Check if a fiscal year exists for the given date and company
     * @param startDate The start date to check
     * @param company The company to check
     * @return The existing fiscal year if found, null otherwise
     */
    FiscalYear getFiscalYearByDateAndCompany(LocalDate startDate, String company);

    /**
     * Create a new fiscal year in Frappe
     * @param startDate The start date of the fiscal year
     * @param company The company for which to create the fiscal year
     * @return The created fiscal year
     */
    FiscalYear createFiscalYear(LocalDate startDate, String company);
} 
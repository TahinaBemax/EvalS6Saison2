package itu.mg.rh.services;

import itu.mg.rh.models.PayrollEntry;
import java.util.List;

public interface PayrollEntryService {
    List<PayrollEntry> getPayrollEntry(String name, String company);
    List<PayrollEntry> findAll();
    boolean save(PayrollEntry payrollEntry);
    boolean delete(String name);
    PayrollEntry findById(String name);
    boolean update(PayrollEntry payrollEntry);
}
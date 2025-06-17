package itu.mg.rh.services;

import itu.mg.rh.models.SalaryComponent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SalaryComponentService {
    List<SalaryComponent> findAll();
    SalaryComponent findById(String name);
    boolean delete(String name);
    boolean update(SalaryComponent salaryComponent);
    boolean save(SalaryComponent salaryComponent);
}

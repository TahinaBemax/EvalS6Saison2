package itu.mg.rh.services;

import itu.mg.rh.models.Employee;

import java.util.List;

public interface EmployeeService {
    List<Employee> getEmployee(String fullName, String departement);
    List<Employee> findAll();
    boolean save(Employee employee);
    boolean delete(String name);
    Employee findById(String name);
    boolean update(Employee employee);
}

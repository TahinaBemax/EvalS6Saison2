package itu.mg.rh.services;

import itu.mg.rh.models.Employee;

import java.util.List;

public interface EmployeeService {
    List<Employee> getEmployee(String fullName, String departement);
}

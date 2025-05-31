package itu.mg.rh.services.impl;

import itu.mg.rh.csv.service.ImportCsv;
import itu.mg.rh.models.Employee;
import itu.mg.rh.services.EmployeeService;
import itu.mg.rh.services.MainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final MainService mainService;
    public static final Logger logger = LoggerFactory.getLogger(ImportCsv.class);

    @Autowired
    public EmployeeServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    public List<Employee> getEmployee(String fullName, String departement) {
        return null;
    }
}

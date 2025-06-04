package itu.mg.rh.services;

import itu.mg.rh.models.Company;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CompanyService {
    List<Company> findAll();
}

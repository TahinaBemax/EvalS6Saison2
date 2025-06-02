package itu.mg.rh.models;

import lombok.Data;

@Data
public class SalaryDetail {
    String salaryComponent;
    String abbr;
    Double amount;
    String condition;
    String formula;
    Integer amountBasedOnFormula;
    String parent; //salary structure_name or salary slip_name
    String parentType; //Salary structure or Salary Slip
    String parentField;
}

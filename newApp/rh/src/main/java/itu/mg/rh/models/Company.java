package itu.mg.rh.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {
    String name;
    String companyName;
    String abbr;
    String defaultCurrency;
    String country;
}

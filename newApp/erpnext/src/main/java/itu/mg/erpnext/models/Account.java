package itu.mg.erpnext.models;

import lombok.Data;

@Data
public class Account {
    private String name;
    private String account_type;
    private String company;
    private String account_currency;
}
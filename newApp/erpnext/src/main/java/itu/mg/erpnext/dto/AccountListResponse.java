package itu.mg.erpnext.dto;

import itu.mg.erpnext.models.Account;
import lombok.Data;

import java.util.List;

@Data
public class AccountListResponse {
    private List<Account> data;
}


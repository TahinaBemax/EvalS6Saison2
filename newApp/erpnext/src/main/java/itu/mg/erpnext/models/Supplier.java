package itu.mg.erpnext.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class Supplier {
    @NotEmpty
    @NotBlank
    private String name;
    private String owner;
    private String naming_series;
    @NotEmpty
    @NotBlank
    private String supplier_name;
    private String country;
    private String supplier_group;
    private String supplier_type;
}

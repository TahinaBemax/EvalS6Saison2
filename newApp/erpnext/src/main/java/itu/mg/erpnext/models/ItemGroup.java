package itu.mg.erpnext.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemGroup {
    @NotNull
    private String name;
    @NotNull
    private String item_group_name;
}

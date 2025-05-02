package itu.mg.erpnext.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class UpdateSupQuotaItemPriceFormData {
    @NotNull
    String id;

    @PositiveOrZero
    double pastPrice;

    @PositiveOrZero
    double newPrice;
}

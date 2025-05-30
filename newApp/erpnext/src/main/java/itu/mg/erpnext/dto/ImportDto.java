package itu.mg.erpnext.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImportDto {
    @NotNull
    MultipartFile supplierFile;
    @NotNull
    MultipartFile SupplierQuotation;
    @NotNull
    MultipartFile requestForQuotation;
}

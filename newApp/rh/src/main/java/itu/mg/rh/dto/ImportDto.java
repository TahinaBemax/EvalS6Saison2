package itu.mg.rh.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImportDto {
    @NotNull
    MultipartFile employeeFile;
    @NotNull
    MultipartFile salaryComponentFile;
    @NotNull
    MultipartFile salarySlipFile;
}

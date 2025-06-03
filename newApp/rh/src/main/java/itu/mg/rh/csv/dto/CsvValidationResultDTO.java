package itu.mg.rh.csv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CsvValidationResultDTO {
    private String line;
    private boolean isValid;
    private String errorMessage;
    private int lineNumber;
}

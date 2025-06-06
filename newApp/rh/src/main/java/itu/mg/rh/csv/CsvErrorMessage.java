package itu.mg.rh.csv;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CsvErrorMessage {
    String fileName;
    String fieldName;
    String message;
    Object line;
}

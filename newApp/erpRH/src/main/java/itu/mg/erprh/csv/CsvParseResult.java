package itu.mg.erprh.csv;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CsvParseResult<T> {
    private List<T> validRows;
    private List<CsvErrorMessage> errors;

    public boolean isValid(){
        return errors.size() == 0;
    }
    public long countValidRows(){
        return validRows.size();
    }
    public long countInvalidRows(){
        return errors.size();
    }
}

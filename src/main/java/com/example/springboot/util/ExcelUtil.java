package com.example.springboot.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtil {
    
    public static List<Map<String, Object>> readExcel(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            
            List<Map<String, Object>> data = new ArrayList<>();
            Row headerRow = sheet.getRow(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                Map<String, Object> rowData = new HashMap<>();
                for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                    String header = getCellValue(headerRow.getCell(j));
                    rowData.put(header, getCellValue(row.getCell(j)));
                }
                data.add(rowData);
            }
            
            return data;
        }
    }
    
    public static void writeExcel(OutputStream outputStream, List<Map<String, Object>> data, List<String> headers) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");
        
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            headerRow.createCell(i).setCellValue(headers.get(i));
        }
        
        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Map<String, Object> rowData = data.get(i);
            
            for (int j = 0; j < headers.size(); j++) {
                Object value = rowData.get(headers.get(j));
                if (value != null) {
                    if (value instanceof Number) {
                        row.createCell(j).setCellValue(((Number) value).doubleValue());
                    } else {
                        row.createCell(j).setCellValue(value.toString());
                    }
                }
            }
        }
        
        workbook.write(outputStream);
        workbook.close();
    }
    
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case Cell.CELL_TYPE_BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case Cell.CELL_TYPE_FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
    
    public static <T> List<T> convertToObjects(List<Map<String, Object>> data, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        // 可以使用BeanUtils或其他工具进行转换
        return result;
    }
}

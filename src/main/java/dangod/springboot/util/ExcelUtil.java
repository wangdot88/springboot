package dangod.springboot.util;

import dangod.springboot.dto.FitnessTestImportDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelUtil {

    public static List<FitnessTestImportDTO> importFitnessTest(MultipartFile file) throws IOException {
        List<FitnessTestImportDTO> list = new ArrayList<>();
        InputStream inputStream = file.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            FitnessTestImportDTO dto = new FitnessTestImportDTO();

            Cell cell0 = row.getCell(0);
            if (cell0 != null) {
                dto.setStudentNo(cell0.getStringCellValue().trim());
            }

            Cell cell1 = row.getCell(1);
            if (cell1 != null) {
                dto.setName(cell1.getStringCellValue().trim());
            }

            Cell cell2 = row.getCell(2);
            if (cell2 != null) {
                dto.setYear((int) cell2.getNumericCellValue());
            }

            Cell cell3 = row.getCell(3);
            if (cell3 != null) {
                dto.setSemester((int) cell3.getNumericCellValue());
            }

            Cell cell4 = row.getCell(4);
            if (cell4 != null) {
                dto.setRun1000(cell4.getNumericCellValue());
            }

            Cell cell5 = row.getCell(5);
            if (cell5 != null) {
                dto.setRun800(cell5.getNumericCellValue());
            }

            Cell cell6 = row.getCell(6);
            if (cell6 != null) {
                dto.setRun50(cell6.getNumericCellValue());
            }

            Cell cell7 = row.getCell(7);
            if (cell7 != null) {
                dto.setSitAndReach((int) cell7.getNumericCellValue());
            }

            Cell cell8 = row.getCell(8);
            if (cell8 != null) {
                dto.setStandingLongJump((int) cell8.getNumericCellValue());
            }

            Cell cell9 = row.getCell(9);
            if (cell9 != null) {
                dto.setSitUp((int) cell9.getNumericCellValue());
            }

            Cell cell10 = row.getCell(10);
            if (cell10 != null) {
                dto.setPullUp((int) cell10.getNumericCellValue());
            }

            Cell cell11 = row.getCell(11);
            if (cell11 != null) {
                dto.setBmi(cell11.getNumericCellValue());
            }

            list.add(dto);
        }

        workbook.close();
        inputStream.close();
        return list;
    }

    public static Workbook createWorkbook() {
        return new XSSFWorkbook();
    }

    public static Sheet createSheet(Workbook workbook, String sheetName) {
        return workbook.createSheet(sheetName);
    }

    public static Row createRow(Sheet sheet, int rowNum) {
        return sheet.createRow(rowNum);
    }

    public static Cell createCell(Row row, int column) {
        return row.createCell(column);
    }

    public static void setCellValue(Cell cell, String value) {
        cell.setCellValue(value);
    }

    public static void setCellValue(Cell cell, Double value) {
        cell.setCellValue(value);
    }

    public static void setCellValue(Cell cell, Integer value) {
        cell.setCellValue(value);
    }

    public static void setCellValue(Cell cell, Date value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        cell.setCellValue(sdf.format(value));
    }

    public static void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
    }
}

package cn.zswltech.mithras.service.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ExcelKeywordReplacerUtil {
//    public static void main(String[] args) throws IOException {
//        FileInputStream inputStream = new FileInputStream("C:\\Users\\LENOVO\\Desktop\\运营相关优化汇总0411.xlsx");
//        Workbook workbook = new XSSFWorkbook(inputStream);
//
//        Map<String, String> rendMap = new HashMap<>();
//        rendMap.put("oldKeyword", "newKeyword1");
//
//        replaceKeywordInWorkbook(workbook, rendMap);
//
//        FileOutputStream outputStream = new FileOutputStream("C:\\\\Users\\\\LENOVO\\\\Desktop\\\\modified_example.xlsx");
//        workbook.write(outputStream);
//        workbook.close();
//        inputStream.close();
//        outputStream.close();
//    }

    public static void replaceKeywordInWorkbook(Workbook workbook, Map<String, String> rendMap) {
        for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            for (Row row : sheet) {
                for (Cell cell : row) {
                    switch (cell.getCellType()) {
                        case STRING:
                            String oldValue = cell.getStringCellValue();
                            rendMap.forEach((k, v) -> {
                                String key = "{{" + k + "}}";
                                if (oldValue.contains(key)) {
                                    String newValue = oldValue.replace(key, v);
                                    cell.setCellValue(newValue);
                                }
                            });
                            break;
                        // Handle other cell types (if necessary)
                        default:
                            break;
                    }
                }
            }
        }
    }
}

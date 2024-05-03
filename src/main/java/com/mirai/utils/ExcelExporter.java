package com.mirai.utils;

import com.mirai.models.response.UserResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class ExcelExporter {

    public static ByteArrayInputStream exportToExcel(List<UserResponse> tasks) throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Tasks");

        // Create header row dynamically
        Row header = sheet.createRow(0);
        Field[] fields = UserResponse.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            header.createCell(i).setCellValue(fields[i].getName());
        }

        // Add data rows
        int rowNum = 1;
        for (UserResponse task : tasks) {
            Row row = sheet.createRow(rowNum++);
            int cellNum = 0;
            for (Field field : fields) {
                field.setAccessible(true); // Make the field accessible (in case it's private)
                try {
                    Object value = field.get(task);
                    if (value != null) {
                        row.createCell(cellNum).setCellValue(value.toString());
                    } else {
                        row.createCell(cellNum).setCellValue("");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                cellNum++;
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}

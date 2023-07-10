package com.temp.manager.domain.excel;

import com.temp.manager.domain.Temp;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelExporter {

    public static void exportToExcel(List<Temp> tempList) {


        Workbook workbook = new XSSFWorkbook(); // 새로운 Workbook 생성
        Sheet sheet = workbook.createSheet("DailyData"); // "DailyData"라는 이름의 시트 생성
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

        // Header 생성
        Row header = sheet.createRow(0);
        String[] headerNames = {"ID", "IP", "Timestamp", "CPU Temperature",
                "CPU Usage", "GPU Temperature", "GPU Usage", "State"};

        // 헤더의 각 이름을 셀에 지정
        for (int i = 0; i < headerNames.length; i++) {
            Cell headerCell = header.createCell(i);
            headerCell.setCellValue(headerNames[i]);
        }

        // tempList의 데이터를 반복하며 엑셀 파일에 데이터 입력
        for (int i = 0; i < tempList.size(); i++) {
            Temp temp = tempList.get(i);
            Row dataRow = sheet.createRow(i + 1);

            float cpuTemp = (temp.getCpuTemp() != null) ? temp.getCpuTemp() : 0.0f;
            float cpuUsage = (temp.getCpuUsage() != null) ? temp.getCpuUsage() : 0.0f;
            float gpuTemp = (temp.getGpuTemp() != null) ? temp.getGpuTemp() : 0.0f;
            float gpuUsage = (temp.getGpuUsage() != null) ? temp.getGpuUsage() : 0.0f;

            dataRow.createCell(0).setCellValue(temp.getId());
            dataRow.createCell(1).setCellValue(temp.getIp());
            dataRow.createCell(2).setCellValue(dateTimeFormatter.format(temp.getDateTime()));
            dataRow.createCell(3).setCellValue(cpuTemp);
            dataRow.createCell(4).setCellValue(cpuUsage);
            dataRow.createCell(5).setCellValue(gpuTemp);
            dataRow.createCell(6).setCellValue(gpuUsage);
            dataRow.createCell(7).setCellValue(temp.getState());
        }

        // 각 컬럼의 너비를 셀 내용에 맞춰서 조정
        for (int i = 0; i < headerNames.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Excel 파일 저장
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate now = LocalDate.now();
        String filename = dateFormatter.format(now) + ".xlsx";
        String saveFilePath = "D:\\Web\\TempDir"; // 직접 지정한 경로

        // 수정한 부분
        Path outputPath = Paths.get(saveFilePath, filename); // 지정한 경로와 파일명을 결합

        try (FileOutputStream fileOut = new FileOutputStream(outputPath.toString())) {
            workbook.write(fileOut); // 파일 저장
        } catch (IOException e) {
            e.printStackTrace(); // 오류 출력
        }
    }
}
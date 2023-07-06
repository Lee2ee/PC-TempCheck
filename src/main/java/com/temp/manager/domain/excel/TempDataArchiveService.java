package com.temp.manager.domain.excel;


import com.temp.manager.domain.Temp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TempDataArchiveService {

    @Autowired
    private TempArchiveRepository tempArchiveRepository;
    // ..다른 필요한 의존성이나 구성요소 자동 주입...

    @Scheduled(cron = "30 59 23 * * ?") // 매일 23시 59분 30초에 실행
//    @Scheduled(cron = "30 */5 * * * *")
    public void exportDailyData() {
        List<Temp> dailyData = fetchDailyData(); // 데이터베이스로부터 하루치 데이터 가져오기

        ExcelExporter.exportToExcel(dailyData); // dailyData를 전달하여 Excel 파일로 저장

        // 데이터가 기록된 후 테이블의 모든 행을 제거합니다.
        tempArchiveRepository.truncateTable();
    }

    public List<Temp> fetchDailyData() {
        // 현재 날짜에 대한 LocalDateTime 값
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        // 오늘 날짜와 일치하는 기록을 가져오는 쿼리
        return tempArchiveRepository.findTempsByDateTimeBetween(today.atStartOfDay(), today.plusDays(1).atStartOfDay());
    }
}
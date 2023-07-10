package com.temp.manager.domain.excel.updown;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.stream.Stream;

@Component
public class UploadFile {
    @Autowired
    private LocalFileRepository localFileRepository;

    @Scheduled(cron = "0 * * * * ?") // 매 분 실행, 원하는 스케줄로 조정 가능
    public void updateDatabaseWithNewFiles() {
        try {
            String folderPath = "D:\\Web\\TempDir";
            Stream<Path> paths = Files.list(Paths.get(folderPath));

            paths.filter(Files::isRegularFile)
                    .filter(path -> !path.getFileName().toString().startsWith("~$"))
                    .forEach(path -> {
                        try {
                            String fileName = path.getFileName().toString();
                            if (!localFileRepository.existsByFileName(fileName)) {
                                BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
                                LocalDateTime creationTime = LocalDateTime.ofInstant(attributes.creationTime().toInstant(), ZoneId.systemDefault());

                                LocalFile localFile = new LocalFile();
                                localFile.setFileName(fileName);
                                localFile.setFilePath(path.toAbsolutePath().toString());
                                localFile.setCreationTime(creationTime); // 포맷팅된 문자열을 저장합니다.
                                localFileRepository.save(localFile);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

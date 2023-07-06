package com.temp.manager.domain.excel.updown;

import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class LocalFileService {
    private final LocalFileRepository localFileRepository;

    public LocalFileService(LocalFileRepository localFileRepository) {
        this.localFileRepository = localFileRepository;
    }

    public List<LocalFile> getAllLocalFiles() {
        return localFileRepository.findAll();
    }

    public LocalFile getLocalFileById(Long id) {
        return localFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("File not found with id: " + id));
    }
}

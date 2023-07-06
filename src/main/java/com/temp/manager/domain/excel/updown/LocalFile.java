package com.temp.manager.domain.excel.updown;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "temparchive")
@Getter
@Setter
public class LocalFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "filename")
    private String fileName;

    @Column(name = "filepath")
    private String filePath;

    @Column(name = "datetime")
    private LocalDateTime creationTime;

}
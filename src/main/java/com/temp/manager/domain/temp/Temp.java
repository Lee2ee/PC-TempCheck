package com.temp.manager.domain.temp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "tempdata")
@NoArgsConstructor
@Getter
public class Temp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ip")
    private String ip;

    @Column(name = "timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;

    @Column(name = "cpu_temp")
    private Float cpuTemp;

    @Column(name = "cpu_usage")
    private Float cpuUsage;

    @Column(name = "gpu_temp")
    private Float gpuTemp;

    @Column(name = "gpu_usage")
    private Float gpuUsage;

    @Column(name = "state")
    private String state;

    @Builder
    public Temp(String ip, LocalDateTime dateTime, Float cpuTemp, Float cpuUsage, Float gpuTemp, Float gpuUsage, String state) {
        this.ip = ip;
        this.dateTime = dateTime;
        this.cpuTemp = cpuTemp;
        this.cpuUsage = cpuUsage;
        this.gpuTemp = gpuTemp;
        this.gpuUsage = gpuUsage;
        this.state = state;
    }
}
package com.temp.manager.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
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

    private static final LocalDateTime baseTime = LocalDateTime.now();

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

    public Temp withState(String state) {
        return Temp.builder()
                .ip(this.ip)
                .dateTime(this.dateTime)
                .cpuTemp(this.cpuTemp)
                .cpuUsage(this.cpuUsage)
                .gpuTemp(this.gpuTemp)
                .gpuUsage(this.gpuUsage)
                .state(state)
                .build();
    }

    public Temp updateStateOnly(String newState) {
        this.state = newState;
        return this;
    }
}
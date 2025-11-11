package com.example.mahasbr.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
public class VisitorCountDTO {
    private Long totalCount;
    private Long todayCount;
    private LocalDate date;

    public VisitorCountDTO() {
    }

    public VisitorCountDTO(Long totalCount, Long todayCount, LocalDate date) {
        this.totalCount = totalCount;
        this.todayCount = todayCount;
        this.date = date;
    }
}
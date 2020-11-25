package ru.taxi.adminpanel.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecordDTO {

    private String from;

    private String to;

    private LocalDateTime begin;

    private LocalDateTime end;

    private Long price;
}

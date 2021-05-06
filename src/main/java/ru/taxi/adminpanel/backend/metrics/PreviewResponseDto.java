package ru.taxi.adminpanel.backend.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreviewResponseDto {

    private List<TripRecordDto> previewData;
    private Integer recordsCount;

}

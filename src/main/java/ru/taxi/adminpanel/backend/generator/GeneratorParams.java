package ru.taxi.adminpanel.backend.generator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class GeneratorParams {

    @NotEmpty
    private String city;
    @NotNull
    private Integer rad;
    @NotNull
    private Integer ordersPerDayNumber;
    @NotEmpty
    private String language;
    @NotEmpty
    private String predictorUrl;
    @NotNull
    private LocalDate tripsDateLeftBorder;
    @NotNull
    private LocalDate tripsDateRightBorder;

    private boolean removePreviouslyGenerated;


}

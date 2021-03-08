package ru.taxi.adminpanel.backend.generator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class GeneratorParams {

    private String city;
    private int rad;
    private int ordersNumber;
    private String language;
}

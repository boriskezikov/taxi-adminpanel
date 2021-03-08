package ru.taxi.adminpanel.backend.generator;

import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.taxi.adminpanel.backend.geoapi.GoogleApiGeoEncoder;
import ru.taxi.adminpanel.backend.geoapi.GoogleApiMapper;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class GeneratorAccessorService {

    private final GeneratorParamsRepository generatorParamsRepository;
    private final GoogleApiGeoEncoder googleApiGeoEncoder;

    public GeneratorParametersEntity updateGeneratorParams(GeneratorParams generatorParams) {
        GeocodingResult encodedResult = googleApiGeoEncoder.encode(generatorParams.getCity());
        LatLng cityGeometry = GoogleApiMapper.retrieveCityGeometry(encodedResult);
        GeneratorParametersEntity parametersEntity = GeneratorParametersEntity.builder()
                .lat(cityGeometry.lat)
                .lng(cityGeometry.lng)
                .ordersNumber(generatorParams.getOrdersNumber())
                .language(generatorParams.getLanguage())
                .rad(generatorParams.getRad())
                .city(generatorParams.getCity())
                .id(BigInteger.ONE)
                .build();
        return generatorParamsRepository.save(parametersEntity);
    }

    public GeneratorParametersEntity loadParameters() {
        return generatorParamsRepository.findAll().stream().findFirst().orElse(null);
    }

}

package ru.taxi.adminpanel.backend.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.taxi.adminpanel.backend.utils.HttpService;

import static ru.taxi.adminpanel.backend.utils.Constants.GENERATOR_GENERATE_ADDRESSES_URL;
import static ru.taxi.adminpanel.backend.utils.Constants.GENERATOR_GENERATE_TRIPS_URL;
import static ru.taxi.adminpanel.backend.utils.Constants.LOAD_GENERATOR_PARAMETERS_URL;
import static ru.taxi.adminpanel.backend.utils.Constants.UPDATE_GENERATOR_PARAMETERS_URL;

@Service
@RequiredArgsConstructor
public class GeneratorAccessorService {

    private final HttpService httpService;

    @Value("${generator.host}")
    private String generatorHost;

    public GeneratorParametersEntity updateGeneratorParams(GeneratorParams generatorParams) {
        return httpService.put(generatorHost + UPDATE_GENERATOR_PARAMETERS_URL, null,
                generatorParams, GeneratorParametersEntity.class);
    }

    public GeneratorParametersEntity loadParameters() {
        return httpService.get(generatorHost + LOAD_GENERATOR_PARAMETERS_URL, null,
                GeneratorParametersEntity.class);
    }

    public void generateTrips() {
        httpService.getAsyncRestTemplate().getForEntity(generatorHost + GENERATOR_GENERATE_TRIPS_URL, Void.class);
    }

    public void generateAddresses() {
        httpService.getAsyncRestTemplate().getForEntity(generatorHost + GENERATOR_GENERATE_ADDRESSES_URL, Void.class);
    }


}

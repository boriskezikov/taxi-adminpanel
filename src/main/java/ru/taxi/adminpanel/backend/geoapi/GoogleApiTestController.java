package ru.taxi.adminpanel.backend.geoapi;

import lombok.RequiredArgsConstructor;
import org.atmosphere.config.service.Get;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.taxi.adminpanel.backend.generator.FakeDataGenerator;

@RestController
@RequiredArgsConstructor
public class GoogleApiTestController {

    private final GoogleApiGeoDecoder googleApiGeoDecoder;
    private final GoogleApiGeoEncoder googleApiGeoEncoder;
    private final FakeDataGenerator fakeDataGenerator;

//    @PostMapping("/test/decode")
//    public List<AddressEntity> testAddressDecoding(@RequestBody LatLng coordinatesDTO) {
//        try {
//            return googleApiGeoDecoder.decode(coordinatesDTO);
//        } catch (InterruptedException | ApiException | IOException e) {
//            throw new ExternalApiException("Error:", e);
//        }
//
//    }
//
//    @PostMapping("/test/encode")
//    public GeocodingResult testAddressEncoding(@RequestBody String city) {
//        try {
//            return googleApiGeoEncoder.encode(city);
//        } catch (InterruptedException | ApiException | IOException e) {
//            throw new ExternalApiException("Error:", e);
//        }
//    }
//
//    @PostMapping("/test/point")
//    public List<LatLng> testRandomPoints(@RequestBody GeneratorParams generatorParams) {
//        return FakeDataGenerator.generateMapPoint(generatorParams);
//    }
    @GetMapping("/test")
    public void test() {
        fakeDataGenerator.generate(null);
    }
}

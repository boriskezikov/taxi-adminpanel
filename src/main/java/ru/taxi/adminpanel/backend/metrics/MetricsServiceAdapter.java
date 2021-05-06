package ru.taxi.adminpanel.backend.metrics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import ru.taxi.adminpanel.backend.utils.HttpService;

import java.net.URI;

import static ru.taxi.adminpanel.backend.metrics.Constants.PREVIEW_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsServiceAdapter {

    private final HttpService httpService;

    @Value("${services.metrics-server.host}")
    private String metricsServerHost;

    public PreviewResponseDto previewData(Integer recordsToLoad) {
        log.info("metrics() - loading {} records for preview", recordsToLoad);
        String requestUri = UriComponentsBuilder.newInstance()
                .host(metricsServerHost)
                .uri(URI.create(PREVIEW_URL))
                .queryParam("records", recordsToLoad).toUriString();
        PreviewResponseDto previewResponseDto = httpService.get(requestUri, null, PreviewResponseDto.class);
        return previewResponseDto;
    }

    public ClusterCenters loadClusteringInfo(){};

    public RawDataInfo loadRawDataInformation() {};

    public PickupPoints loadPickupPoints() {}

}

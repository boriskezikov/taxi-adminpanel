package ru.taxi.adminpanel.backend.utils;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class HttpService {


    private final RestTemplate restTemplate;


    public <T> T post(String url, HttpHeaders headers, Object body, Class<T> responseType) {
        return restTemplate.exchange(url,
                HttpMethod.POST,
                getHttpEntityFromDTO(body, headers),
                responseType
        ).getBody();
    }

    public <T> T get(String url, HttpHeaders headers, Class<T> responseType) {
        return restTemplate.exchange(url,
                HttpMethod.GET,
                getHttpEntityFromDTO(null, headers),
                responseType
        ).getBody();
    }


    private static HttpEntity<String> getHttpEntity(String json, HttpHeaders httpHeaders) {
        if (httpHeaders == null) {
            httpHeaders = new HttpHeaders();
        }
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(json, httpHeaders);
    }

    private static HttpEntity<String> getHttpEntity(HttpHeaders httpHeaders) {
        if (httpHeaders == null) {
            httpHeaders = new HttpHeaders();
        }
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(httpHeaders);
    }

    public static HttpEntity<String> getHttpEntityFromDTO(Object obj, HttpHeaders httpHeaders) {
        return getHttpEntity(new Gson().toJson(obj), httpHeaders);

    }
}

package ru.practicum.statsclient;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.statsdto.dto.HitDto;
import ru.practicum.statsdto.dto.StatsDto;

import java.util.List;
import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<List<StatsDto>> get(String path, @Nullable Map<String, Object> parameters) {
        return statSendRequest(path, parameters);
    }

    protected <T> ResponseEntity<HitDto> post(String path, T body) {
        return hitSendRequest(path, body);
    }

    private <T> ResponseEntity<HitDto> hitSendRequest(String path, T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body);
        ResponseEntity<HitDto> statServerResponse;
        try {
            statServerResponse = rest.exchange(path, HttpMethod.POST, requestEntity, HitDto.class);
        } catch (HttpStatusCodeException e) {
            throw new HttpClientErrorException(e.getStatusCode(), e.getStatusText());
        }
        return statServerResponse;
    }

    private ResponseEntity<List<StatsDto>> statSendRequest(String path, @Nullable Map<String, Object> parameters) {
        ResponseEntity<List<StatsDto>> statServerResponse;
        try {
            if (parameters != null) {
                statServerResponse = rest.exchange(path, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                }, parameters);
            } else {
                statServerResponse = rest.exchange(path, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });
            }
        } catch (HttpStatusCodeException e) {
            throw new HttpClientErrorException(e.getStatusCode(), e.getStatusText());
        }
        return statServerResponse;
    }

}

package ru.practicum.statsclient;

import org.springframework.http.*;
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

    protected ResponseEntity<StatsDto> get(String path, @Nullable Map<String, Object> parameters) {
        return makeStatAndSendRequest(HttpMethod.GET, path, null, parameters, null);
    }

    protected <T> ResponseEntity<HitDto> post(String path, T body) {
        return post(path, null, null, body);
    }

    protected <T> ResponseEntity<HitDto> post(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeHitAndSendRequest(HttpMethod.POST, path, userId, parameters, body);
    }

    private <T> ResponseEntity<HitDto> makeHitAndSendRequest(HttpMethod method, String path, Long userId, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<HitDto> shareitServerResponse;
        try {
            if (parameters != null) {
                shareitServerResponse = rest.exchange(path, method, requestEntity, HitDto.class, parameters);
            } else {
                shareitServerResponse = rest.exchange(path, method, requestEntity, HitDto.class);
            }
        } catch (HttpStatusCodeException e) {
//            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
            throw new HttpClientErrorException(e.getStatusCode(), e.getStatusText());
        }
        return prepareGatewayResponseHit(shareitServerResponse);
    }

    private <T> ResponseEntity<StatsDto> makeStatAndSendRequest(HttpMethod method, String path, Long userId, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<StatsDto> shareitServerResponse;
        try {
            if (parameters != null) {
                shareitServerResponse = rest.exchange(path, method, requestEntity, StatsDto.class, parameters);
            } else {
                shareitServerResponse = rest.exchange(path, method, requestEntity, StatsDto.class);
            }
        } catch (HttpStatusCodeException e) {
//            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
            throw new HttpClientErrorException(e.getStatusCode(), e.getStatusText());
        }
        return prepareGatewayResponseStat(shareitServerResponse);
    }

    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    private static ResponseEntity<StatsDto> prepareGatewayResponseStat(ResponseEntity<StatsDto> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

    private static ResponseEntity<HitDto> prepareGatewayResponseHit(ResponseEntity<HitDto> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }


}

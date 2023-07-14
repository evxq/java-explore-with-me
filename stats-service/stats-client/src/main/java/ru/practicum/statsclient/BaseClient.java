package ru.practicum.statsclient;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.statsdto.dto.HitDto;
import ru.practicum.statsdto.dto.StatsDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<List<StatsDto>> get(String path, @Nullable Map<String, Object> parameters) {
        return makeStatAndSendRequest(path, parameters);
    }

    protected <T> ResponseEntity<HitDto> post(String path, T body) {
        return makeHitAndSendRequest(path, body);
    }

    private <T> ResponseEntity<HitDto> makeHitAndSendRequest(String path, T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body);
        ResponseEntity<HitDto> statServerResponse;
        try {
            statServerResponse = rest.exchange(path, HttpMethod.POST, requestEntity, HitDto.class);
        } catch (HttpStatusCodeException e) {
//            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
            throw new HttpClientErrorException(e.getStatusCode(), e.getStatusText());
        }
        return prepareGatewayResponseHit(statServerResponse);
    }

    private /*<T>*/ ResponseEntity<List<StatsDto>> makeStatAndSendRequest(String path, @Nullable Map<String, Object> parameters) {
//        HttpEntity<T> requestEntity = new HttpEntity<>(null);

        ResponseEntity<List<StatsDto>> statServerResponse;
//        ResponseEntity<StatsDto[]> statServerResponse;

        TypeReference<List<StatsDto>> ref = new TypeReference<>() { };
        try {
            if (parameters != null) {
                statServerResponse = rest.exchange(path, HttpMethod.GET, null, new ParameterizedTypeReference<>() {}, parameters);         /// СЮДА ПРИХОДИТ getStats
//                statServerResponse = rest.getForEntity(path, StatsDto[].class, parameters);
//                statServerResponse = rest.exchange(path, HttpMethod.GET, requestEntity, Object.class, parameters);         /// СЮДА ПРИХОДИТ getStats
            } else {
                statServerResponse = rest.exchange(path, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
//                statServerResponse = rest.exchange(path, HttpMethod.GET, requestEntity, StatsDto.class);
//                statServerResponse = rest.getForEntity(path, StatsDto[].class);
            }
        } catch (HttpStatusCodeException e) {
//            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
            throw new HttpClientErrorException(e.getStatusCode(), e.getStatusText());
        }
//        List<StatsDto> list = new ArrayList<>(Arrays.asList(statServerResponse.getBody()));

//        return prepareGatewayResponseStat(statServerResponse);
        return statServerResponse;
    }

    /*private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }*/

    /*private static ResponseEntity<List<StatsDto>> prepareGatewayResponseStat(ResponseEntity<StatsDto[]> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
//            List<StatsDto> list = new ArrayList<>(Arrays.asList(response.getBody()));
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }*/

    private static <T> ResponseEntity<T> prepareGatewayResponseHit(ResponseEntity<T> response) {
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

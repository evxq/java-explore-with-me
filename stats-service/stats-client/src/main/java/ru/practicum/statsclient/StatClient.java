package ru.practicum.statsclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.statsdto.dto.HitDto;
import ru.practicum.statsdto.dto.StatsDto;

import java.util.List;
import java.util.Map;

@Service
public class StatClient extends BaseClient {

    private static final String API_HIT = "/hit";
    private static final String API_STAT = "/stats";

    @Autowired
    public StatClient(@Value("${client.url}") String serverUrl, RestTemplateBuilder builder) {
//    public StatClient(@Value("http://localhost:9090") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<HitDto> addHit(HitDto hitDto) {
        return post(API_HIT, hitDto);
    }

//    public ResponseEntity<StatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
    public ResponseEntity<List<StatsDto>> getStats(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique);
        return get(API_STAT + "?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }

}

/*@Service
public class StatClient {

    @Autowired
    final RestTemplate template;
//    final String statUrl;
//    private static final String API_HIT = "/hit";
//    private static final String API_STAT = "/stats";

    *//*@Autowired
    public StatClient(RestTemplate template, @Value("${client.url}") String statUrl) {
        this.template = template;
        this.statUrl = statUrl;
    }*//*
    @Autowired
//    public StatClient(@Value("${client.url}") String serverUrl, RestTemplateBuilder builder) {
    public StatClient(@Value("http://localhost:9090") String serverUrl, RestTemplateBuilder builder) {
        this.template = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }
    public ResponseEntity<StatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique);
        return template.getForEntity("/stats?start={start}&end={end}&uris={uris}&unique={unique}", StatsDto.class, parameters);
    }

    public void addHit(HitDto hitDto) {
        template.postForObject("/hit", hitDto, HitDto.class);
    }

}*/

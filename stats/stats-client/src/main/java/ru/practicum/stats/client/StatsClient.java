package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stats.dto.StatsHitDto;
import ru.practicum.stats.dto.StatsViewDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatsClient {
    private final String applicationName;
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    protected final RestTemplate rest;


    @Autowired
    public StatsClient(@Value("http://stats-server:9090") String serverUrl,
                       @Value("ewm-main-service") String applicationName,
                       RestTemplateBuilder builder) {
        rest = builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> {
                    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
                            = new HttpComponentsClientHttpRequestFactory();
                    clientHttpRequestFactory.setConnectTimeout(1000);
                    return clientHttpRequestFactory;
                })
                .build();
        this.applicationName = applicationName;
    }

    public void hitStat(HttpServletRequest httpServletRequest) {
        StatsHitDto statsHitDto = StatsHitDto.builder()
                .app(applicationName)
                .ip(httpServletRequest.getRemoteAddr())
                .uri(httpServletRequest.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        HttpEntity<StatsHitDto> requestEntity = new HttpEntity<>(statsHitDto);
        rest.exchange("/hit", HttpMethod.POST, requestEntity, Object.class);

    }

    public List<StatsViewDto> getStats(List<String> uris, LocalDateTime start, LocalDateTime end) {
        if (uris == null || uris.isEmpty()) {
            return List.of();
        }

        MultiValueMap<String, String> myParams = new LinkedMultiValueMap<>();

        myParams.add("start", start.format(DATE_TIME_FORMATTER));
        myParams.add("end", end.format(DATE_TIME_FORMATTER));

        for (String uri : uris) {
            myParams.add("uris", uri);
        }

        String uri = UriComponentsBuilder.fromUriString("/stats")
                .queryParams(myParams)
                .build()
                .toUriString();
        ResponseEntity<List<StatsViewDto>> statsViewDtos = rest.exchange(uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        return statsViewDtos.getBody();
    }
}

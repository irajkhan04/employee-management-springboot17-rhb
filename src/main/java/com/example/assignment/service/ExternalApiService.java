package com.example.assignment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ExternalApiService {
    private final RestClient restClient;
    @Value("${external.api.url}")
    private String externalApiUrl;
    public ExternalApiService(RestClient restClient) {
        this.restClient = restClient;
    }

    public ExternalApiStatus checkGoogle() {
        try {
            HttpStatusCode status = restClient.get()
                    .uri(externalApiUrl)
                    .retrieve()
                    .toBodilessEntity()
                    .getStatusCode();
            return new ExternalApiStatus(externalApiUrl, status.value(), true);
        } catch (Exception ex) {
            // Do not expose internal exception details to API consumers.
            return new ExternalApiStatus(externalApiUrl, 0, false);
        }
    }

    public record ExternalApiStatus(String url, int upstreamStatus, boolean reachable) {
    }
}

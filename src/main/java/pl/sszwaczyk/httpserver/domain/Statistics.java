package pl.sszwaczyk.httpserver.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Statistics {

    private long totalRequests;
    private Map<String, Long> requestsPerUser;

    public Statistics() {
        totalRequests = 0L;
        requestsPerUser = new HashMap<>();
    }

    public void updateStats(String userId) {
        totalRequests++;
        Long requests = requestsPerUser.get(userId);
        if(requests == null) {
            requestsPerUser.put(userId, 1L);
        } else {
            requestsPerUser.put(userId, requests + 1);
        }
    }
}

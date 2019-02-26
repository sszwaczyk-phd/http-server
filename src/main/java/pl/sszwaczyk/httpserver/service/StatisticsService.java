package pl.sszwaczyk.httpserver.service;

import org.springframework.stereotype.Service;
import pl.sszwaczyk.httpserver.domain.Statistics;

@Service
public class StatisticsService {

    private Statistics stats = new Statistics();

    public void updateStats(String userId) {
        stats.updateStats(userId);
    }

    public Statistics getStats() {
        return stats;
    }

}

package pl.sszwaczyk.httpserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sszwaczyk.httpserver.domain.Statistics;
import pl.sszwaczyk.httpserver.service.StatisticsService;

@RestController
public class StatisticsController {

    private StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/stats")
    public Statistics getStats() {
        return statisticsService.getStats();
    }
}

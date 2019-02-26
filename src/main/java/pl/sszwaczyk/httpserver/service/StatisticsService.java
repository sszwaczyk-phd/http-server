package pl.sszwaczyk.httpserver.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import pl.sszwaczyk.httpserver.domain.Statistics;

import javax.annotation.PreDestroy;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class StatisticsService {

    private Statistics stats = new Statistics();

    @PreDestroy
    private void destroy() {
        log.info("Saving statistics to file before destroy...");
        snapshotToFile("./stats-on-exit.xlsx");
    }

    public void updateStats(String userId) {
        stats.updateStats(userId);
    }

    public Statistics getStats() {
        return stats;
    }

    public String snapshotToFile(String statsFile) {
        log.info("Saving statistics to file...");
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Statistics");

        Row noRequestsRow = sheet.createRow(1);
        noRequestsRow.createCell(0).setCellValue("No. requests");

        Map<String, Long> requestsPerUser = stats.getRequestsPerUser();
        Row usersRow = sheet.createRow(0);
        AtomicInteger i = new AtomicInteger(1);
        requestsPerUser.forEach((key, value) -> {
            usersRow.createCell(i.get()).setCellValue(key);
            noRequestsRow.createCell(i.get()).setCellValue(value);
            i.getAndIncrement();
        });

        Row totalRequestsRow = sheet.createRow(3);
        totalRequestsRow.createCell(0).setCellValue("Total requests");
        totalRequestsRow.createCell(1).setCellValue(stats.getTotalRequests());

        try (FileOutputStream fos = new FileOutputStream(statsFile)) {
            workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Cannot save statistics to file " + statsFile + " because " + e.getMessage());
        }

        log.info("Snapshot of statistics saved to " + statsFile);
        return statsFile;
    }

}

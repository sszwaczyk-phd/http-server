package pl.sszwaczyk.httpserver.controller;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.sszwaczyk.httpserver.domain.User;
import pl.sszwaczyk.httpserver.service.ServiceService;
import pl.sszwaczyk.httpserver.service.StatisticsService;
import pl.sszwaczyk.httpserver.service.UserService;
import pl.sszwaczyk.httpserver.utils.ThrottlingInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Slf4j
@RestController
public class FilesController {

    private UserService userService;
    private ServiceService serviceService;

    private StatisticsService statisticsService;

    public FilesController(UserService userService,
                           ServiceService serviceService,
                           StatisticsService statisticsService) {
        this.userService = userService;
        this.serviceService = serviceService;
        this.statisticsService = statisticsService;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getFile(@RequestParam("path") String path, HttpServletRequest request) throws IOException {
        String ip = request.getRemoteAddr();
        User user = userService.getUserByIp(ip);
        if(user == null) {
            log.error("Got request from unknown user with ip " + ip);
            throw new RuntimeException("User not found for ip " + ip);
        }
        log.info("Got request from user " + user.getId() + " with ip " + ip);

        log.info("Updating statistics...");
        statisticsService.updateStats(user.getId());

        File file = openFile(path);
        Resource resource = prepareResource(file, serviceService.getBandwidth());
        return ResponseEntity.ok(resource);
    }

    private Resource prepareResource(File file, Double bitsPerSecond) throws FileNotFoundException {
        final InputStream inputStream = new FileInputStream(file);
        final RateLimiter throttler = RateLimiter.create((bitsPerSecond * 1000000.0) / 8.0);
        final ThrottlingInputStream throttlingInputStream = new ThrottlingInputStream(inputStream, throttler);
        return new InputStreamResource(throttlingInputStream);
    }

    private File openFile(String path) {
        return new File(path);
    }

}

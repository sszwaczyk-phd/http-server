package pl.sszwaczyk.httpserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.sszwaczyk.httpserver.domain.User;
import pl.sszwaczyk.httpserver.service.StatisticsService;
import pl.sszwaczyk.httpserver.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Slf4j
@Controller
public class FilesController {

    private final static int BYTES_DOWNLOAD = 1000;

    private UserService userService;

    private StatisticsService statisticsService;

    public FilesController(UserService userService, StatisticsService statisticsService) {
        this.userService = userService;
        this.statisticsService = statisticsService;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void getFile(@RequestParam("path") String path, HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        try(final InputStream myFile = new FileInputStream(file)) {
            response.setHeader("Content-Length", String.valueOf(file.length()));
            IOUtils.copy(myFile, response.getOutputStream());
        }

    }

    private File openFile(String path) {
        return new File(path);
    }

}

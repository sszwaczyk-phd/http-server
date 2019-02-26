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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Controller
public class FilesController {

    private UserService userService;

    private StatisticsService statisticsService;

    public FilesController(UserService userService, StatisticsService statisticsService) {
        this.userService = userService;
        this.statisticsService = statisticsService;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] getFile(@RequestParam("path") String path,
                                        HttpServletRequest request) throws IOException {
        String ip = request.getRemoteAddr();
        User user = userService.getUserByIp(ip);
        if(user == null) {
            log.error("Got request from unknown user with ip " + ip);
            throw new RuntimeException("User not found for ip " + ip);
        }
        log.info("Got request from user " + user.getId() + " with ip " + ip);

        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = IOUtils.toByteArray(fis);

        statisticsService.updateStats(user.getId());

        return bytes;
    }

}

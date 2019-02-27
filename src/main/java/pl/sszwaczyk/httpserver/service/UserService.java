package pl.sszwaczyk.httpserver.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.sszwaczyk.httpserver.domain.User;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserService {

    @Value("${usersFile}")
    private String usersFile;

    private Map<String, User> users;

    @PostConstruct
    private void init() throws IOException {
        log.info("Loading users from file " + usersFile);
        File file = new File(usersFile);
        ObjectMapper objectMapper = new ObjectMapper();
        List<User> usersLists = objectMapper.readValue(file, new TypeReference<List<User>>(){});

        users = new HashMap<>();
        usersLists.forEach(user -> users.put(user.getId(), user));
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public User getUserById(String id) {
        return users.get(id);
    }

    public User getUserByIp(String ip) {
        return users.values().stream()
                .filter(u -> u.getIp().equals(ip))
                .findFirst()
                .orElse(null);
    }

}

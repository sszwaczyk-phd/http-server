package pl.sszwaczyk.httpserver.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import pl.sszwaczyk.httpserver.domain.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
public class ServiceService {

    @Value("${servicesFile}")
    private String servicesFile;

    private Double bandwidth;

    @PostConstruct
    private void init() throws IOException {
        log.info("Loading services from file " + servicesFile);
        File file = new File(servicesFile);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Service> services = objectMapper.readValue(file, new TypeReference<List<Service>>(){});

        List<String> ips = getMachineIps();
        for(Service s: services) {
            if(ips.contains(s.getIp())) {
                bandwidth = s.getBandwidth();
                break;
            }
        }

        if(bandwidth == 0) {
            throw new RuntimeException("Cannot read required bandwidth from services config file");
        }

        log.info("Required bandwidth initialized to " + bandwidth + " b/s");
    }

    private List<String> getMachineIps() throws SocketException {
        List<String> ips = new ArrayList<>();
        Enumeration e = NetworkInterface.getNetworkInterfaces();
        while(e.hasMoreElements())
        {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements())
            {
                InetAddress i = (InetAddress) ee.nextElement();
                String ip = i.toString();
                ip = ip.substring(ip.indexOf("/") + 1);
                ips.add(ip);
                log.info("Loaded ip = " + ip);
            }
        }
        return ips;
    }

    public Double getBandwidth() {
        return bandwidth;
    }

}

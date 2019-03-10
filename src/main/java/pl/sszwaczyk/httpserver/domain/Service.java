package pl.sszwaczyk.httpserver.domain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
public class Service {

    private String id;
    private String ip;
    private Integer port;
    private String path;
    private Double bandwidth;
    private Double maxUneven;
    private Long maxLatency;

}

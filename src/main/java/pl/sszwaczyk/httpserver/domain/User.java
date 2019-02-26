package pl.sszwaczyk.httpserver.domain;

import lombok.Data;

@Data
public class User {

    private String id;
    private String ip;
    private String datapathId;

}

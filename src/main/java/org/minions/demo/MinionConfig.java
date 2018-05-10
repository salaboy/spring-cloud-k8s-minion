package org.minions.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "minion")
public class MinionConfig {

    private String type = "generic-minion";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

package org.minions.demo;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service

public class BossClientService {

    private final RestTemplate restTemplate;

    private static final Log log = LogFactory.getLog(BossClientService.class);

    public BossClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @HystrixCommand(fallbackMethod = "getFallbackName", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")
    })
    public String requestMission(String to,
                                 String from) {

        String url = String.format("%s/mission/%s",
                                      to,
                                      from);

        log.info("--- Requesting a task to Boss: " + url);
        return this.restTemplate.getForObject(url,
                                              String.class);
    }

    private String getFallbackName(String to,
                                   String from) {
        return "This Boss  (" + to + ") not available now, please come back later (Fallback) client:" + from;
    }
}

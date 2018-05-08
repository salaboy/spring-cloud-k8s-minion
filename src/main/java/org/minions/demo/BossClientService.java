package org.minions.demo;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class BossClientService {
    private final RestTemplate restTemplate;

    public BossClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @HystrixCommand(fallbackMethod = "getFallbackName", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")
    })
    public String requestMission(String to, String from) {
        return this.restTemplate.getForObject(String.format("http://%s/mission/%s", to, from), String.class);
    }

    private String getFallbackName(int delay) {
        return "This Boss not available now, please come back later (Fallback)";
    }
}

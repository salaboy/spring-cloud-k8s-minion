package org.minions.demo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import feign.Feign;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@EnableDiscoveryClient
@Import(FeignClientsConfiguration.class)
public class Application implements CommandLineRunner {

    private static final Log log = LogFactory.getLog(Application.class);
    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${spring.application.name}")
    private String appName;

    public static void main(String[] args) {
        SpringApplication.run(Application.class,
                              args);
    }

    @Override
    public void run(String... args) throws Exception {

        log.info("Minion Started! ");
    }

    @Scheduled(fixedRate = 2000)
    public void isThereGoldOutThere() throws UnknownHostException {
        List<String> services = this.discoveryClient.getServices();
        boolean missionRequested = false;
        for (String s : services) {
            List<ServiceInstance> instances = this.discoveryClient.getInstances(s);
            for (ServiceInstance si : instances) {
                Map<String, String> metadata = si.getMetadata();
                String type = metadata.get("type");
                if ("boss".equals(type)) {
                    String from = appName + "@" + InetAddress.getLocalHost().getHostName();
                    String url = "http://" + si.getHost() + ":" + si.getPort();
                    log.info("--- Requesting a task to Boss: " + url + " -> from: " + from);
                    Boss boss = Feign.builder().target(Boss.class,
                                                       url);
                    boss.requestMission(from);
                    missionRequested = true;
                }
            }
        }
        if (!missionRequested) {
            log.info("--- NO BOSS FOUND! :( ");
        }
    }
}

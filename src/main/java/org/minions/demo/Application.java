package org.minions.demo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import static org.minions.demo.BossClientService.FIND_A_BOSS_TASK;

@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
@EnableCircuitBreaker
public class Application implements CommandLineRunner {

    private static final Log log = LogFactory.getLog(Application.class);

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private BossClientService bossClient;

    @Autowired
    private MinionConfig minionConfig;

    private String taskAtHand = FIND_A_BOSS_TASK;

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Value("${spring.application.name}")
    private String appName;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Minion (" + appName + ":" + minionConfig.getType() + ")Started! ");
    }

    /*
     * Every 10 seconds look for a boss or keep working on the task at hand
     */
    @Scheduled(fixedRate = 10000)
    public void doSomeWork() throws UnknownHostException {
        if (taskAtHand.equals(FIND_A_BOSS_TASK)) {
            taskAtHand = findANewBoss();
            if (taskAtHand.equals(FIND_A_BOSS_TASK)) {
                log.info(">>> NO BOSS FOUND, I will keep looking for one ");
            }
        }
        log.info(">>> Working on " + taskAtHand);
    }

    /*
     * Every 60 seconds if you are not looking for a Boss, wrap up the task at hand
     */
    @Scheduled(fixedRate = 60000)
    public void finishWork() {
        if (!taskAtHand.equals(FIND_A_BOSS_TASK)) {
            log.info(">>> Finishing " + taskAtHand);
            taskAtHand = FIND_A_BOSS_TASK;
        }
    }


    /*
     * Find a new boss by filtering the available services based on Metadata
     */
    private String findANewBoss() throws UnknownHostException {
        List<String> services = this.discoveryClient.getServices();

        for (String service : services) {
            List<ServiceInstance> instances = this.discoveryClient.getInstances(service);
            for (ServiceInstance se : instances) {
                Map<String, String> metadata = se.getMetadata();
                String type = metadata.get("type");
                if ("boss".equals(type)) {

                    String from = appName + "@" + InetAddress.getLocalHost().getHostName();
                    String url = "http://" + se.getServiceId();
                    return bossClient.requestMission(url, from);
                }
            }
        }
        return FIND_A_BOSS_TASK;
    }
}

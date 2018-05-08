package org.minions.demo;

import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("worker")
public interface Boss {

    @RequestLine("POST /mission/{minion}")
    public void requestMission(@Param("minion") String minion);
}

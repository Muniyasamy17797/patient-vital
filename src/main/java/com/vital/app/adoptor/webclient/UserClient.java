package com.vital.app.adoptor.webclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.vital.app.domain.dto.UserInfo;
import com.vital.app.infrastructure.config.FeignConfig;

/**
 * Feign client to interact with the User service.
 */
@FeignClient(name = "microProject", configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/api/users/{id}")
    UserInfo getUserById(@PathVariable("id") Long id);
}


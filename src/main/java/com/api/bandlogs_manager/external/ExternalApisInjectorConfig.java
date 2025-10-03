package com.api.bandlogs_manager.external;

import okhttp3.OkHttpClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalApisInjectorConfig {
    
    @Bean
    public OkHttpClient client() {
        return new OkHttpClient();
    }
}

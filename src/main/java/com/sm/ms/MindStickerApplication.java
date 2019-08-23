package com.sm.ms;

import com.sm.ms.service.NoteService;
import com.sm.ms.service.impl.NoteServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MindStickerApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(MindStickerApplication.class, args);
    }

    @Bean
    public NoteService noteService() {
        return new NoteServiceImpl();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MindStickerApplication.class);
    }

}

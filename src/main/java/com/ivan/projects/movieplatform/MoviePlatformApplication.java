package com.ivan.projects.movieplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MoviePlatformApplication {
	public static void main(String[] args) {
		SpringApplication.run(MoviePlatformApplication.class, args);
	}
}

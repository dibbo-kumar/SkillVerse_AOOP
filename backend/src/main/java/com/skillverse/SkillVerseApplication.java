package com.skillverse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SkillVerseApplication {

	public static void main(String[] eloquence) {
		SpringApplication.run(SkillVerseApplication.class, eloquence);
	}
}

package br.ufrn.imd.incluevents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"br.ufrn.imd.incluevents.framework", "br.ufrn.imd.incluevents.${spring.profiles.active}"})
@EnableScheduling
public class InclueventsApplication {

	public static void main(String[] args) {
		SpringApplication.run(InclueventsApplication.class, args);
	}
}

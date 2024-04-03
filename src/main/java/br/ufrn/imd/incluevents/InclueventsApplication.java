package br.ufrn.imd.incluevents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InclueventsApplication {

	public static void main(String[] args) {
		SpringApplication.run(InclueventsApplication.class, args);
	}

}

package com.example.maintenance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BarrierMaintenanceReportHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarrierMaintenanceReportHubApplication.class, args);
	}
}
package machineRental.MR;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@SpringBootApplication
public class MachineRentalApplication {

	public static void main(String[] args) {
		SpringApplication.run(MachineRentalApplication.class, args);
	}
}

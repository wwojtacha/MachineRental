package machineRental.MR;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@SpringBootApplication
public class MachineRentalApplication {

	public static void main(String[] args) {
		SpringApplication.run(MachineRentalApplication.class, args);
	}
}

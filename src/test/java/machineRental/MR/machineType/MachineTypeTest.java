package machineRental.MR.machineType;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import machineRental.MR.exception.BindingResultException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.machineType.model.MachineType;
import machineRental.MR.machineType.service.MachineTypeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

@RunWith(SpringRunner.class)
@SpringBootTest
// ActiveProfile indicates which DB should be used. 'mr_test' DB will be used in this case as defined in application-test.properties file.
@ActiveProfiles("test")
public class MachineTypeTest {

	@Autowired
	private MachineTypeService machineTypeService;

//	for JUnit 4
//	@Test(expected = NotFoundException.class)
	@Test
	public void shouldThrowNotFoundException() {
		MachineType editedMachineType = new MachineType(83L, "Wozidło59", CostCategory.TRANSPORT);
		BindingResult bindingResult = new BeanPropertyBindingResult(editedMachineType, "machineType");

//		can`t update MachineType if its id does not exist in DB
		Exception exception = assertThrows(NotFoundException.class, () -> {
			machineTypeService.update(14L, editedMachineType, bindingResult);
		});

		String expectedMessage = "does not exist";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	public void shouldNotUpdateMachineType() {
		MachineType editedMachineType = new MachineType(83L, "Wozidło59", CostCategory.TRANSPORT);
		BindingResult bindingResult = new BeanPropertyBindingResult(editedMachineType, "machineType");

//		can`t update machineType field of MachineType with id 80 to machineType field of MachineType with id 83
//		as it means these are two different MachineType instances and both of them already exist in DB
		assertThrows(BindingResultException.class, () -> {
			machineTypeService.update(80L, editedMachineType, bindingResult);
		});

		String expectedMessage = "already exists";
		String actualMessage = bindingResult.getFieldError().getDefaultMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

}

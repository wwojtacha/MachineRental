package machineRental.MR.client.controller;

import machineRental.MR.client.model.Client;
import machineRental.MR.client.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Client create(@RequestBody @Valid Client client, BindingResult bindingResult) {
        return clientService.create(client, bindingResult);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<Client> search(
            @RequestParam(name = "mpk", required = false, defaultValue = "") String mpk,
            @RequestParam(name = "name", required = false, defaultValue = "") String name,
            @RequestParam(name = "city", required = false, defaultValue = "") String city,
            @RequestParam(name = "postalCode", required = false, defaultValue = "") String postalCode,
            @RequestParam(name = "email", required = false, defaultValue = "") String email,
            @RequestParam(name = "contactPerson", required = false, defaultValue = "") String contactPerson,
            @RequestParam(name = "phoneNumber", required = false, defaultValue = "") String phoneNumber,
            Pageable pageable) {
        return clientService.search(mpk, name, city, postalCode, email, contactPerson, phoneNumber, pageable);
    }

    @GetMapping("/{clientMPK}")
    @ResponseStatus(HttpStatus.OK)
    public Client getClientByMPK(@PathVariable String clientMPK) {
        return clientService.getByMpk(clientMPK);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Client update(@PathVariable Long id, @RequestBody @Valid Client client, BindingResult bindingResult) {
        return clientService.update(id, client, bindingResult);
    }



}

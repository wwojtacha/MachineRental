package machineRental.MR.client.service;

import machineRental.MR.client.model.Client;
import machineRental.MR.exception.BindingResultException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public Client create(Client client, BindingResult bindingResult) {

        validateClientMpkConsistency(client.getMpk(), null, bindingResult);
        return clientRepository.save(client);
    }


    public Page<Client> search(String mpk, String name, String city, String postalCode, String email, String contactPerson, String phoneNumber, Pageable pageable) {
        return clientRepository.findByMpkContainingAndNameContainingAndCityContainingAndPostalCodeContainingAndEmailContainingAndContactPersonContainingAndPhoneNumberContaining(mpk, name, city, postalCode, email, contactPerson, phoneNumber, pageable);
    }

    public Client getByMpk(String mpk) {
        Optional<Client> dbClient = Optional.ofNullable(clientRepository.findByMpk(mpk));

        if (!dbClient.isPresent()) {
            throw new NotFoundException(String.format("Client with MPK: \'%s\' does not exist", mpk));
        }

        return dbClient.get();
    }

    public Client update(Long id, Client client, BindingResult bindingResult) {
        Optional<Client> dbClient = clientRepository.findById(id);
        if(!dbClient.isPresent()) {
            throw new NotFoundException(String.format("Client with id: \'%s\' does not exist", id));
        }

        validateClientMpkConsistency(client.getMpk(), dbClient.get().getMpk(), bindingResult);

        client.setId(id);
        return clientRepository.save(client);
    }

    private void validateClientMpkConsistency(String mpk, String currentMpk, BindingResult bindingResult) {
        if(clientRepository.existsByMpk(mpk) && !mpk.equals(currentMpk)) {
            bindingResult.addError(new FieldError(
                    "client",
                    "mpk",
                    String.format("Client with MPK/NIP: \'%s\' already exists", mpk)));
        }

        if(bindingResult.hasErrors()) {
            throw new BindingResultException(bindingResult);
        }
    }

}

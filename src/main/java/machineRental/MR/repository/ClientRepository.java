package machineRental.MR.repository;

import machineRental.MR.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByMpk(String mpk);

    Page<Client> findByMpkContainingAndNameContainingAndCityContainingAndPostalCodeContainingAndEmailContainingAndContactPersonContainingAndPhoneNumberContaining(
            String mpk,
            String name,
            String city,
            String postalCode,
            String email,
            String contactPerson,
            String phoneNumber,
            Pageable pageable
    );

}

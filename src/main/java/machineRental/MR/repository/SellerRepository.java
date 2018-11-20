package machineRental.MR.repository;


import machineRental.MR.model.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    boolean existsByMpk(String mpk);

    Page<Seller> findByMpkContainingAndNameContainingAndCityContaining(
            String mpk,
            String name,
            String city,
            Pageable pageable
    );

}

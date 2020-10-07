package machineRental.MR.repository;

import machineRental.MR.material.model.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    boolean existsByType(String type);

    Material findByType(String type);

    Page<Material> findByTypeContaining(String type, Pageable pageable);

}

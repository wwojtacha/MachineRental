package machineRental.MR.repository;

import machineRental.MR.operator.model.Operator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperatorRepository extends JpaRepository<Operator, Long> {

  boolean existsByName(String name);

  Page<Operator> findByNameContainingAndQualificationsContainingAndCompany_MpkContaining(String name, String qualifications, String companyMpk, Pageable pageable);

  Page<Operator> findByNameContainingAndQualificationsContaining(String name, String qualifications, Pageable pageable);
}

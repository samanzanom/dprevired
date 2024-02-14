package cl.previred.challenge.repository;

import cl.previred.challenge.entity.Worker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {
    Optional<Worker> findByRut(String rut);

    Page<Worker> findAllByCompany_Id(Pageable pageable, String companyId);
}

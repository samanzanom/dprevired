package cl.previred.challenge.service;

import cl.previred.challenge.controller.dto.WorkerRequest;
import cl.previred.challenge.entity.Company;
import cl.previred.challenge.entity.Worker;
import cl.previred.challenge.exceptions.DuplicateException;
import cl.previred.challenge.exceptions.NotFoundException;
import cl.previred.challenge.repository.CompanyRepository;
import cl.previred.challenge.repository.WorkerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class WorkerService {

    private static final String NOT_FOUND_EXCEPTION = "Worker does not exist, id: %s";

    private final WorkerRepository repository;
    private final CompanyRepository companyRepository;

    public WorkerService(WorkerRepository repository, CompanyRepository companyRepository) {
        this.repository = repository;
        this.companyRepository = companyRepository;
    }

    @Transactional
    public Worker create(WorkerRequest request) {

        String rut = request.rut();

        Optional<Worker> existingWorker = repository.findByRut(rut);
        if (existingWorker.isPresent()) {
            throw new DuplicateException(String.format("Worker with the rut '%s' already exists.", rut));
        }

        Optional<Company> company = companyRepository.findById(request.companyId());
        if (company.isEmpty()) {
            throw  new NotFoundException(String.format("Company whit the id '%s' no exists.", request.companyId()));
        }

        rut = rut.replace(".", "");

        Worker worker = new Worker();
        worker.setRut(rut);
        worker.setNames(request.names());
        worker.setFirstSurname(request.firstSurname());
        worker.setSecondSurname(request.secondSurname());
        worker.setCompany(company.get());
        return repository.save(worker);
    }

    @Transactional
    public Worker update(Long id, WorkerRequest request) {
        Worker worker = repository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format(NOT_FOUND_EXCEPTION, id)));

        worker.setRut(request.rut());
        worker.setNames(request.names());
        worker.setFirstSurname(request.firstSurname());
        worker.setSecondSurname(request.secondSurname());
        return repository.save(worker);
    }

    @Transactional
    public void delete(Long id) {
        Worker worker = repository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format(NOT_FOUND_EXCEPTION, id)));
        repository.delete(worker);
    }

    public Worker get(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format(NOT_FOUND_EXCEPTION, id)));
    }

    public Page<Worker> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Worker> findAllByCompanyId(Pageable pageable, String companyId) {
        return repository.findAllByCompany_Id(pageable,companyId);
    }
}

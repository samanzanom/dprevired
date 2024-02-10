package cl.previred.challenge.service;

import cl.previred.challenge.controller.dto.CompanyRequest;
import cl.previred.challenge.entity.Company;
import cl.previred.challenge.exceptions.DuplicateException;
import cl.previred.challenge.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CompanyService {

    private final CompanyRepository repository;

    public CompanyService(CompanyRepository repository) {
        this.repository = repository;
    }
    @Transactional
    public Company create(CompanyRequest request) {
        String rut = request.rut();
        String companyName = request.companyName();
        Optional<Company> existingUser = repository.findByRut(rut);
        if (existingUser.isPresent()) {
            throw new DuplicateException(String.format("Company with the rut '%s' already exists.", rut));
        }

        rut = rut.replace(".", "");

        // Texto Base: 3 primeras letras del nombre de la empresa
        String baseText = companyName.length() > 3 ? companyName.substring(0, 3) : companyName;

        // Fecha y Hora Actual
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String dateTime = sdf.format(new Date());


        Company company = new Company();
        company.setId(baseText+dateTime);
        company.setRut(rut);
        company.setCompanyName(request.companyName());
        return repository.save(company);
    }
}

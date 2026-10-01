package com.inetum.demo.services.onetoone;

import com.inetum.demo.domain.onetoone.Phone;
import com.inetum.demo.domain.onetoone.PhoneDetails;
import com.inetum.demo.dtos.PhoneDTO;
import com.inetum.demo.patch.MergePatchService;
import com.inetum.demo.repositories.onetoone.PhoneDetailsRepository;
import com.inetum.demo.repositories.onetoone.PhoneRepository;
import jakarta.persistence.criteria.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Optional;

@Service
public class OneToOneService {
    private static final int MAX_PAGE_SIZE = 100;

    PhoneRepository phoneRepository;
    PhoneDetailsRepository phoneDetailsRepository;
    private final MergePatchService mergePatch;

    @Autowired
    OneToOneService(
            PhoneRepository phoneRepository,
            PhoneDetailsRepository phoneDetailsRepository,
            MergePatchService mergePatch
    ){
        this.phoneRepository = phoneRepository;
        this.phoneDetailsRepository = phoneDetailsRepository;
        this.mergePatch = mergePatch;
    }

    /** Siembra un teléfono de ejemplo (PepePhone / 5G) y devuelve todos los teléfonos. */
    @Transactional
    public List<Phone> seed(){
        Phone phone = new Phone();
        phone.setNumber("923124578");
        this.phoneRepository.save(phone);
        PhoneDetails phoneDetails = new PhoneDetails();
        phoneDetails.setProvider("PepePhone");
        phoneDetails.setTechnology("5G");
        this.phoneDetailsRepository.save(phoneDetails);
        phone.setDetails(phoneDetails);
        this.phoneRepository.save(phone);
        return this.phoneRepository.findAll();
    }

    public List<Phone> listado(){
        return this.phoneRepository.findPhonesByDetails_Provider("PepePhone");
    }

    @Transactional(readOnly = true)
    public List<Phone> findAll() {
        return this.phoneRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Phone> findById(Long id) {
        return this.phoneRepository.findById(id);
    }

    /**
     * Búsqueda paginada; todos los filtros son opcionales y se combinan con AND.
     *
     * @param number     contiene (sin distinguir mayúsculas)
     * @param provider   igual, sin distinguir mayúsculas (exige que el teléfono tenga details)
     * @param technology igual, sin distinguir mayúsculas (exige que el teléfono tenga details)
     * @param page       página, empezando en 1
     * @param size       tamaño de página (entre 1 y 100)
     */
    @Transactional(readOnly = true)
    public Page<Phone> search(String number, String provider, String technology, int page, int size) {
        Specification<Phone> spec = Specification.unrestricted();
        if (hasText(number)) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("number")), "%" + number.trim().toLowerCase() + "%"));
        }
        if (hasText(provider)) {
            spec = spec.and((root, query, cb) -> cb.equal(
                    cb.lower(root.join("details", JoinType.INNER).get("provider")), provider.trim().toLowerCase()));
        }
        if (hasText(technology)) {
            spec = spec.and((root, query, cb) -> cb.equal(
                    cb.lower(root.join("details", JoinType.INNER).get("technology")), technology.trim().toLowerCase()));
        }
        int safePage = Math.max(page, 1) - 1;
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return this.phoneRepository.findAll(spec, PageRequest.of(safePage, safeSize, Sort.by("id").ascending()));
    }

    @Transactional
    public Phone create(PhoneDTO dto) {
        Phone phone = new Phone();
        dto.applyTo(phone);
        return this.phoneRepository.save(phone);
    }

    /** Sustitución completa (PUT): lo que no venga en el DTO (details) se elimina. */
    @Transactional
    public Optional<Phone> update(Long id, PhoneDTO dto) {
        return this.phoneRepository.findById(id).map(phone -> {
            dto.applyTo(phone);
            return this.phoneRepository.save(phone);
        });
    }

    /**
     * Modificación parcial (JSON Merge Patch): solo cambia lo enviado, incluidos los campos de details.
     *
     * @throws jakarta.validation.ConstraintViolationException si el resultado no es válido
     *         (no se modifica nada, porque el parche se aplica sobre una copia)
     */
    @Transactional
    public Optional<Phone> patch(Long id, JsonNode patch) {
        return this.phoneRepository.findById(id).map(phone -> {
            PhoneDTO parcheado = this.mergePatch.apply(PhoneDTO.from(phone), patch);
            parcheado.applyTo(phone);
            return this.phoneRepository.save(phone);
        });
    }

    /** Borra el teléfono y sus details (cascade). */
    @Transactional
    public Optional<Phone> delete(Long id) {
        return this.phoneRepository.findById(id).map(phone -> {
            this.phoneRepository.delete(phone);
            return phone;
        });
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
}

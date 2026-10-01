package com.inetum.demo.services.onetoone;

import com.inetum.demo.domain.onetoone.Phone;
import com.inetum.demo.domain.onetoone.PhoneDetails;
import com.inetum.demo.dtos.PhoneDTO;
import com.inetum.demo.dtos.PhoneDetailsDTO;
import com.inetum.demo.patch.MergePatchService;
import com.inetum.demo.repositories.onetoone.PhoneDetailsRepository;
import com.inetum.demo.repositories.onetoone.PhoneRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OneToOneServiceTest {

    @Mock
    private PhoneRepository phoneRepository;
    @Mock
    private PhoneDetailsRepository phoneDetailsRepository;
    @Mock
    private MergePatchService mergePatch;
    @InjectMocks
    private OneToOneService service;

    private static PhoneDTO dto(String number, String provider, String technology) {
        PhoneDTO dto = new PhoneDTO();
        dto.setNumber(number);
        if (provider != null) {
            PhoneDetailsDTO d = new PhoneDetailsDTO();
            d.setProvider(provider);
            d.setTechnology(technology);
            dto.setDetails(d);
        }
        return dto;
    }

    private static Phone phoneConDetails() {
        return new Phone(1L, "600111222", new PhoneDetails(7L, "PepePhone", "5G"));
    }

    @Test
    void createBuildsPhoneWithDetailsFromDto() {
        given(phoneRepository.save(any(Phone.class))).willAnswer(i -> i.getArgument(0));

        Phone creado = service.create(dto("600111222", "PepePhone", "5G"));

        assertThat(creado.getNumber()).isEqualTo("600111222");
        assertThat(creado.getDetails().getProvider()).isEqualTo("PepePhone");
    }

    @Test
    void updateKeepsExistingDetailsInstance() {
        Phone existente = phoneConDetails();
        given(phoneRepository.findById(1L)).willReturn(Optional.of(existente));
        given(phoneRepository.save(existente)).willReturn(existente);

        Optional<Phone> r = service.update(1L, dto("700333444", "Movistar", "4G"));

        assertThat(r).isPresent();
        assertThat(r.get().getDetails().getId()).isEqualTo(7L);
        assertThat(r.get().getDetails().getProvider()).isEqualTo("Movistar");
    }

    @Test
    void updateWithoutDetailsRemovesThem() {
        Phone existente = phoneConDetails();
        given(phoneRepository.findById(1L)).willReturn(Optional.of(existente));
        given(phoneRepository.save(existente)).willReturn(existente);

        assertThat(service.update(1L, dto("600111222", null, null))).get()
                .extracting(Phone::getDetails).isNull();
    }

    @Test
    void updateOfMissingPhoneReturnsEmptyAndSavesNothing() {
        given(phoneRepository.findById(9L)).willReturn(Optional.empty());

        assertThat(service.update(9L, dto("600111222", null, null))).isEmpty();
        verify(phoneRepository, never()).save(any());
    }

    @Test
    void patchAppliesTheMergedDtoOverTheEntity() {
        Phone existente = phoneConDetails();
        PhoneDTO parcheado = dto("611222333", "PepePhone", "4G");
        given(phoneRepository.findById(1L)).willReturn(Optional.of(existente));
        given(mergePatch.apply(any(PhoneDTO.class), any())).willReturn(parcheado);
        given(phoneRepository.save(existente)).willReturn(existente);

        Optional<Phone> r = service.patch(1L, new JsonMapper().createObjectNode());

        assertThat(r).get().extracting(Phone::getNumber).isEqualTo("611222333");
        assertThat(r.get().getDetails().getTechnology()).isEqualTo("4G");
    }

    @Test
    void patchOfMissingPhoneReturnsEmpty() {
        given(phoneRepository.findById(9L)).willReturn(Optional.empty());

        assertThat(service.patch(9L, new JsonMapper().createObjectNode())).isEmpty();
    }

    @Test
    void deleteReturnsTheDeletedPhone() {
        Phone existente = phoneConDetails();
        given(phoneRepository.findById(1L)).willReturn(Optional.of(existente));

        assertThat(service.delete(1L)).contains(existente);
        verify(phoneRepository).delete(existente);
    }

    @Test
    void deleteOfMissingPhoneReturnsEmpty() {
        given(phoneRepository.findById(9L)).willReturn(Optional.empty());

        assertThat(service.delete(9L)).isEmpty();
        verify(phoneRepository, never()).delete(any(Phone.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchClampsPageAndSize() {
        given(phoneRepository.findAll(any(Specification.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of()));
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

        Page<Phone> r = service.search(null, " ", null, -5, 5000);

        verify(phoneRepository).findAll(any(Specification.class), captor.capture());
        assertThat(r).isEmpty();
        assertThat(captor.getValue().getPageNumber()).isZero();   // page < 1 -> primera página
        assertThat(captor.getValue().getPageSize()).isEqualTo(100);   // size topado a 100
    }
}

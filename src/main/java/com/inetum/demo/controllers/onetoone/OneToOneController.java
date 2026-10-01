package com.inetum.demo.controllers.onetoone;

import com.inetum.demo.domain.onetoone.Phone;
import com.inetum.demo.dtos.PageResponse;
import com.inetum.demo.dtos.PhoneDTO;
import com.inetum.demo.patch.MergePatchOperation;
import com.inetum.demo.services.onetoone.OneToOneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tools.jackson.databind.JsonNode;

import java.net.URI;
import java.util.List;

/**
 * CRUD de Phone (relación OneToOne con PhoneDetails): el cuerpo de POST/PUT/PATCH lleva los details
 * anidados ({"number": "923124578", "details": {"provider": "PepePhone", "technology": "5G"}}).
 */
@RestController
@RequestMapping("/api/v1/onetoone")
public class OneToOneController {
    OneToOneService oneToOneService;

    @Autowired
    public OneToOneController(
            OneToOneService oneToOneService
    ){
        this.oneToOneService = oneToOneService;
    }

    @GetMapping("/")
    @Operation(summary = "list phones", description = "Lists every phone with its details", tags = {"phone"})
    public ResponseEntity<List<Phone>> index(){
        return ResponseEntity.ok(this.oneToOneService.findAll());
    }

    /** Siembra un teléfono de ejemplo (antes lo hacía GET /, que ahora solo lista). */
    @PostMapping("/seed")
    @Operation(summary = "seed an example phone", tags = {"phone"})
    public ResponseEntity<List<Phone>> seed(){
        return new ResponseEntity<>(this.oneToOneService.seed(), HttpStatus.OK);
    }

    @GetMapping("/provider")
    public ResponseEntity<List<Phone>> getPepephone(){
        return ResponseEntity.ok(this.oneToOneService.listado());
    }

    /** Todos los filtros son opcionales; page empieza en 1 y size se limita a 100. */
    @GetMapping("/search")
    @Operation(summary = "search phones",
            description = "Filters by number (contains), provider and technology (equal, ignoring case); paginated",
            tags = {"phone"})
    public ResponseEntity<PageResponse<Phone>> search(
            @RequestParam(required = false) String number,
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) String technology,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(PageResponse.from(
                this.oneToOneService.search(number, provider, technology, page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "show a phone", tags = {"phone"})
    public ResponseEntity<Phone> show(@PathVariable Long id){
        return ResponseEntity.ok(this.oneToOneService.findById(id).orElseThrow(() -> notFound(id)));
    }

    @PostMapping("/")
    @Operation(summary = "create a phone", description = "Creates a phone and, if sent, its details", tags = {"phone"})
    public ResponseEntity<Phone> create(@Valid @RequestBody PhoneDTO dto){
        Phone phone = this.oneToOneService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{id}").buildAndExpand(phone.getId()).toUri();
        return ResponseEntity.created(location).body(phone);
    }

    @PutMapping("/{id}")
    @Operation(summary = "replace a phone",
            description = "Full replacement: a phone sent without details loses its details", tags = {"phone"})
    public ResponseEntity<Phone> replace(@PathVariable Long id, @Valid @RequestBody PhoneDTO dto){
        return ResponseEntity.ok(this.oneToOneService.update(id, dto).orElseThrow(() -> notFound(id)));
    }

    @MergePatchOperation
    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Phone> patch(
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/merge-patch+json",
                            examples = @ExampleObject(value = "{\"details\": {\"technology\": \"4G\"}}")))
            @RequestBody JsonNode patch){
        return ResponseEntity.ok(this.oneToOneService.patch(id, patch).orElseThrow(() -> notFound(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete a phone", description = "Deletes the phone and its details", tags = {"phone"})
    public ResponseEntity<Phone> delete(@PathVariable Long id){
        return ResponseEntity.ok(this.oneToOneService.delete(id).orElseThrow(() -> notFound(id)));
    }

    private static ResourceNotFoundException notFound(Long id) {
        return new ResourceNotFoundException("Not found with id = " + id);
    }
}

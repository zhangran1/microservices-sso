package com.microservices.poc.applicationone.web.rest;

import com.microservices.poc.applicationone.domain.Appone;
import com.microservices.poc.applicationone.repository.ApponeRepository;
import com.microservices.poc.applicationone.repository.UserRepository;
import com.microservices.poc.applicationone.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.microservices.poc.applicationone.domain.Appone}.
 */
@RestController
@RequestMapping("/api")
public class ApponeResource {

    private final Logger log = LoggerFactory.getLogger(ApponeResource.class);

    private static final String ENTITY_NAME = "applicationoneAppone";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ApponeRepository apponeRepository;

    private final UserRepository userRepository;

    public ApponeResource(ApponeRepository apponeRepository, UserRepository userRepository) {
        this.apponeRepository = apponeRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST  /appones} : Create a new appone.
     *
     * @param appone the appone to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new appone, or with status {@code 400 (Bad Request)} if the appone has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/appones")
    public ResponseEntity<Appone> createAppone(@Valid @RequestBody Appone appone) throws URISyntaxException {
        log.debug("REST request to save Appone : {}", appone);
        if (appone.getId() != null) {
            throw new BadRequestAlertException("A new appone cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (appone.getUser() != null) {
            // Save user in case it's new and only exists in gateway
            userRepository.save(appone.getUser());
        }
        Appone result = apponeRepository.save(appone);
        return ResponseEntity
            .created(new URI("/api/appones/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /appones/:id} : Updates an existing appone.
     *
     * @param id the id of the appone to save.
     * @param appone the appone to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appone,
     * or with status {@code 400 (Bad Request)} if the appone is not valid,
     * or with status {@code 500 (Internal Server Error)} if the appone couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/appones/{id}")
    public ResponseEntity<Appone> updateAppone(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Appone appone
    ) throws URISyntaxException {
        log.debug("REST request to update Appone : {}, {}", id, appone);
        if (appone.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appone.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!apponeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        if (appone.getUser() != null) {
            // Save user in case it's new and only exists in gateway
            userRepository.save(appone.getUser());
        }
        Appone result = apponeRepository.save(appone);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, appone.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /appones/:id} : Partial updates given fields of an existing appone, field will ignore if it is null
     *
     * @param id the id of the appone to save.
     * @param appone the appone to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appone,
     * or with status {@code 400 (Bad Request)} if the appone is not valid,
     * or with status {@code 404 (Not Found)} if the appone is not found,
     * or with status {@code 500 (Internal Server Error)} if the appone couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/appones/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Appone> partialUpdateAppone(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Appone appone
    ) throws URISyntaxException {
        log.debug("REST request to partial update Appone partially : {}, {}", id, appone);
        if (appone.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appone.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!apponeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        if (appone.getUser() != null) {
            // Save user in case it's new and only exists in gateway
            userRepository.save(appone.getUser());
        }

        Optional<Appone> result = apponeRepository
            .findById(appone.getId())
            .map(
                existingAppone -> {
                    if (appone.getName() != null) {
                        existingAppone.setName(appone.getName());
                    }
                    if (appone.getHandle() != null) {
                        existingAppone.setHandle(appone.getHandle());
                    }

                    return existingAppone;
                }
            )
            .map(apponeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, appone.getId().toString())
        );
    }

    /**
     * {@code GET  /appones} : get all the appones.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of appones in body.
     */
    @GetMapping("/appones")
    public List<Appone> getAllAppones() {
        log.debug("REST request to get all Appones");
        return apponeRepository.findAll();
    }

    /**
     * {@code GET  /appones/:id} : get the "id" appone.
     *
     * @param id the id of the appone to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the appone, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/appones/{id}")
    public ResponseEntity<Appone> getAppone(@PathVariable Long id) {
        log.debug("REST request to get Appone : {}", id);
        Optional<Appone> appone = apponeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(appone);
    }

    /**
     * {@code DELETE  /appones/:id} : delete the "id" appone.
     *
     * @param id the id of the appone to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/appones/{id}")
    public ResponseEntity<Void> deleteAppone(@PathVariable Long id) {
        log.debug("REST request to delete Appone : {}", id);
        apponeRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

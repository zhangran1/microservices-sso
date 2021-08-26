package com.microservices.poc.applicationone.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.microservices.poc.applicationone.IntegrationTest;
import com.microservices.poc.applicationone.domain.Appone;
import com.microservices.poc.applicationone.repository.ApponeRepository;
import com.microservices.poc.applicationone.repository.UserRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ApponeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ApponeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_HANDLE = "AAAAAAAAAA";
    private static final String UPDATED_HANDLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/appones";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ApponeRepository apponeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restApponeMockMvc;

    private Appone appone;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Appone createEntity(EntityManager em) {
        Appone appone = new Appone().name(DEFAULT_NAME).handle(DEFAULT_HANDLE);
        return appone;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Appone createUpdatedEntity(EntityManager em) {
        Appone appone = new Appone().name(UPDATED_NAME).handle(UPDATED_HANDLE);
        return appone;
    }

    @BeforeEach
    public void initTest() {
        appone = createEntity(em);
    }

    @Test
    @Transactional
    void createAppone() throws Exception {
        int databaseSizeBeforeCreate = apponeRepository.findAll().size();
        // Create the Appone
        restApponeMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appone))
            )
            .andExpect(status().isCreated());

        // Validate the Appone in the database
        List<Appone> apponeList = apponeRepository.findAll();
        assertThat(apponeList).hasSize(databaseSizeBeforeCreate + 1);
        Appone testAppone = apponeList.get(apponeList.size() - 1);
        assertThat(testAppone.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAppone.getHandle()).isEqualTo(DEFAULT_HANDLE);
    }

    @Test
    @Transactional
    void createApponeWithExistingId() throws Exception {
        // Create the Appone with an existing ID
        appone.setId(1L);

        int databaseSizeBeforeCreate = apponeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restApponeMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appone))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appone in the database
        List<Appone> apponeList = apponeRepository.findAll();
        assertThat(apponeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = apponeRepository.findAll().size();
        // set the field null
        appone.setName(null);

        // Create the Appone, which fails.

        restApponeMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appone))
            )
            .andExpect(status().isBadRequest());

        List<Appone> apponeList = apponeRepository.findAll();
        assertThat(apponeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHandleIsRequired() throws Exception {
        int databaseSizeBeforeTest = apponeRepository.findAll().size();
        // set the field null
        appone.setHandle(null);

        // Create the Appone, which fails.

        restApponeMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appone))
            )
            .andExpect(status().isBadRequest());

        List<Appone> apponeList = apponeRepository.findAll();
        assertThat(apponeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAppones() throws Exception {
        // Initialize the database
        apponeRepository.saveAndFlush(appone);

        // Get all the apponeList
        restApponeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appone.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].handle").value(hasItem(DEFAULT_HANDLE)));
    }

    @Test
    @Transactional
    void getAppone() throws Exception {
        // Initialize the database
        apponeRepository.saveAndFlush(appone);

        // Get the appone
        restApponeMockMvc
            .perform(get(ENTITY_API_URL_ID, appone.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(appone.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.handle").value(DEFAULT_HANDLE));
    }

    @Test
    @Transactional
    void getNonExistingAppone() throws Exception {
        // Get the appone
        restApponeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAppone() throws Exception {
        // Initialize the database
        apponeRepository.saveAndFlush(appone);

        int databaseSizeBeforeUpdate = apponeRepository.findAll().size();

        // Update the appone
        Appone updatedAppone = apponeRepository.findById(appone.getId()).get();
        // Disconnect from session so that the updates on updatedAppone are not directly saved in db
        em.detach(updatedAppone);
        updatedAppone.name(UPDATED_NAME).handle(UPDATED_HANDLE);

        restApponeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAppone.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAppone))
            )
            .andExpect(status().isOk());

        // Validate the Appone in the database
        List<Appone> apponeList = apponeRepository.findAll();
        assertThat(apponeList).hasSize(databaseSizeBeforeUpdate);
        Appone testAppone = apponeList.get(apponeList.size() - 1);
        assertThat(testAppone.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAppone.getHandle()).isEqualTo(UPDATED_HANDLE);
    }

    @Test
    @Transactional
    void putNonExistingAppone() throws Exception {
        int databaseSizeBeforeUpdate = apponeRepository.findAll().size();
        appone.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApponeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appone.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appone))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appone in the database
        List<Appone> apponeList = apponeRepository.findAll();
        assertThat(apponeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAppone() throws Exception {
        int databaseSizeBeforeUpdate = apponeRepository.findAll().size();
        appone.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApponeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appone))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appone in the database
        List<Appone> apponeList = apponeRepository.findAll();
        assertThat(apponeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAppone() throws Exception {
        int databaseSizeBeforeUpdate = apponeRepository.findAll().size();
        appone.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApponeMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appone))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Appone in the database
        List<Appone> apponeList = apponeRepository.findAll();
        assertThat(apponeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateApponeWithPatch() throws Exception {
        // Initialize the database
        apponeRepository.saveAndFlush(appone);

        int databaseSizeBeforeUpdate = apponeRepository.findAll().size();

        // Update the appone using partial update
        Appone partialUpdatedAppone = new Appone();
        partialUpdatedAppone.setId(appone.getId());

        restApponeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppone.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAppone))
            )
            .andExpect(status().isOk());

        // Validate the Appone in the database
        List<Appone> apponeList = apponeRepository.findAll();
        assertThat(apponeList).hasSize(databaseSizeBeforeUpdate);
        Appone testAppone = apponeList.get(apponeList.size() - 1);
        assertThat(testAppone.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAppone.getHandle()).isEqualTo(DEFAULT_HANDLE);
    }

    @Test
    @Transactional
    void fullUpdateApponeWithPatch() throws Exception {
        // Initialize the database
        apponeRepository.saveAndFlush(appone);

        int databaseSizeBeforeUpdate = apponeRepository.findAll().size();

        // Update the appone using partial update
        Appone partialUpdatedAppone = new Appone();
        partialUpdatedAppone.setId(appone.getId());

        partialUpdatedAppone.name(UPDATED_NAME).handle(UPDATED_HANDLE);

        restApponeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppone.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAppone))
            )
            .andExpect(status().isOk());

        // Validate the Appone in the database
        List<Appone> apponeList = apponeRepository.findAll();
        assertThat(apponeList).hasSize(databaseSizeBeforeUpdate);
        Appone testAppone = apponeList.get(apponeList.size() - 1);
        assertThat(testAppone.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAppone.getHandle()).isEqualTo(UPDATED_HANDLE);
    }

    @Test
    @Transactional
    void patchNonExistingAppone() throws Exception {
        int databaseSizeBeforeUpdate = apponeRepository.findAll().size();
        appone.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApponeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, appone.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(appone))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appone in the database
        List<Appone> apponeList = apponeRepository.findAll();
        assertThat(apponeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAppone() throws Exception {
        int databaseSizeBeforeUpdate = apponeRepository.findAll().size();
        appone.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApponeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(appone))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appone in the database
        List<Appone> apponeList = apponeRepository.findAll();
        assertThat(apponeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAppone() throws Exception {
        int databaseSizeBeforeUpdate = apponeRepository.findAll().size();
        appone.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApponeMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(appone))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Appone in the database
        List<Appone> apponeList = apponeRepository.findAll();
        assertThat(apponeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAppone() throws Exception {
        // Initialize the database
        apponeRepository.saveAndFlush(appone);

        int databaseSizeBeforeDelete = apponeRepository.findAll().size();

        // Delete the appone
        restApponeMockMvc
            .perform(delete(ENTITY_API_URL_ID, appone.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Appone> apponeList = apponeRepository.findAll();
        assertThat(apponeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

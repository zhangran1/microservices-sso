package com.microservices.poc.applicationone.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.microservices.poc.applicationone.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ApponeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Appone.class);
        Appone appone1 = new Appone();
        appone1.setId(1L);
        Appone appone2 = new Appone();
        appone2.setId(appone1.getId());
        assertThat(appone1).isEqualTo(appone2);
        appone2.setId(2L);
        assertThat(appone1).isNotEqualTo(appone2);
        appone1.setId(null);
        assertThat(appone1).isNotEqualTo(appone2);
    }
}

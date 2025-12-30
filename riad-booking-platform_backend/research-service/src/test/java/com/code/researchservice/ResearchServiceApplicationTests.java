package com.code.researchservice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basic application test - does not require Elasticsearch.
 * Full integration tests should be run with Elasticsearch available (e.g., via Docker).
 */
class ResearchServiceApplicationTests {

    @Test
    void applicationClassExists() {
        assertThat(ResearchServiceApplication.class).isNotNull();
    }

    @Test
    void mainMethodExists() throws NoSuchMethodException {
        assertThat(ResearchServiceApplication.class.getMethod("main", String[].class)).isNotNull();
    }

}


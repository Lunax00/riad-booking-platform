package com.code.researchservice.repository;

import com.code.researchservice.model.Riad;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface RiadRepository extends ElasticsearchRepository<Riad, String> {
}


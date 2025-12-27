package com.riadbooking.searchservice.repository;

import com.riadbooking.searchservice.model.Riad;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface RiadRepository extends ElasticsearchRepository<Riad, String> {
}

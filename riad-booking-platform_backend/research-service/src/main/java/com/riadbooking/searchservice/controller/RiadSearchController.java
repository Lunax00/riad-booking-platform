package com.riadbooking.searchservice.controller;

import com.riadbooking.searchservice.dto.SearchCriteria;
import com.riadbooking.searchservice.model.Riad;
import com.riadbooking.searchservice.repository.RiadRepository;
import com.riadbooking.searchservice.service.RiadSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RiadSearchController {

    private final RiadSearchService searchService;
    private final RiadRepository riadRepository;

    @PostMapping("/search")
    public List<Riad> search(@RequestBody @Valid SearchCriteria criteria) {
        return searchService.search(criteria);
    }

    // Endpoint de test : indexer/mettre à jour un riad dans Elasticsearch
    @PostMapping("/index")
    public Riad index(@RequestBody @Valid Riad riad) {
        return riadRepository.save(riad);
    }
}

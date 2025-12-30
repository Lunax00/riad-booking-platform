package com.code.researchservice.controller;

import com.code.researchservice.dto.SearchCriteria;
import com.code.researchservice.model.Riad;
import com.code.researchservice.repository.RiadRepository;
import com.code.researchservice.service.RiadSearchService;
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


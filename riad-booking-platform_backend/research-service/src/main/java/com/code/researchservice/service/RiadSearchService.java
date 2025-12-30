package com.code.researchservice.service;

import com.code.researchservice.dto.SearchCriteria;
import com.code.researchservice.model.Riad;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiadSearchService {

    private final ElasticsearchOperations operations;

    public List<Riad> search(SearchCriteria c) {
        Criteria criteria = new Criteria();

        if (StringUtils.hasText(c.getVille())) {
            criteria = criteria.and(new Criteria("ville").is(c.getVille()));
        }

        if (StringUtils.hasText(c.getQuartier())) {
            criteria = criteria.and(new Criteria("quartier").is(c.getQuartier()));
        }

        if (c.getPersonnes() != null && c.getPersonnes() > 0) {
            criteria = criteria.and(new Criteria("capacite").greaterThanEqual(c.getPersonnes()));
        }

        if (c.getPrixMin() != null) {
            criteria = criteria.and(new Criteria("prixParNuit").greaterThanEqual(c.getPrixMin()));
        }

        if (c.getPrixMax() != null) {
            criteria = criteria.and(new Criteria("prixParNuit").lessThanEqual(c.getPrixMax()));
        }

        // Disponibilité simple:
        // disponibleDe <= dateDebut AND disponibleA >= dateFin
        LocalDate d1 = c.getDateDebut();
        LocalDate d2 = c.getDateFin();
        if (d1 != null && d2 != null) {
            criteria = criteria
                    .and(new Criteria("disponibleDe").lessThanEqual(d1))
                    .and(new Criteria("disponibleA").greaterThanEqual(d2));
        }

        if (c.getEquipements() != null && !c.getEquipements().isEmpty()) {
            for (String eq : c.getEquipements()) {
                if (StringUtils.hasText(eq)) {
                    criteria = criteria.and(new Criteria("equipements").is(eq));
                }
            }
        }

        CriteriaQuery query = new CriteriaQuery(criteria);

        SearchHits<Riad> hits = operations.search(query, Riad.class);

        List<Riad> out = new ArrayList<>();
        hits.forEach(h -> out.add(h.getContent()));
        return out;
    }
}

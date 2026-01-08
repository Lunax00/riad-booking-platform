package ma.lunaire.catalogservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.lunaire.catalogservice.api.dto.*;
import ma.lunaire.catalogservice.domain.CatalogItem;
import ma.lunaire.catalogservice.domain.ItemStatus;
import ma.lunaire.catalogservice.exception.ItemNotFoundException;
import ma.lunaire.catalogservice.repository.CatalogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CatalogService {

    private final CatalogRepository catalogRepository;

    public CatalogItemResponse createItem(CreateItemRequest request) {
        log.info("Creating new catalog item: {}", request.getName());

        CatalogItem item = CatalogItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .city(request.getCity())
                .address(request.getAddress())
                .pricePerNight(request.getPricePerNight())
                .capacity(request.getCapacity())
                .numberOfRooms(request.getNumberOfRooms())
                .numberOfBathrooms(request.getNumberOfBathrooms())
                .amenities(request.getAmenities())
                .imageUrl(request.getImageUrl())
                .ownerId(request.getOwnerId())
                .status(ItemStatus.AVAILABLE)
                .build();

        CatalogItem savedItem = catalogRepository.save(item);
        log.info("Created catalog item with ID: {}", savedItem.getId());

        return mapToResponse(savedItem);
    }

    @Transactional(readOnly = true)
    public CatalogItemResponse getItemById(Long id) {
        log.debug("Fetching catalog item with ID: {}", id);
        CatalogItem item = catalogRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Riad non trouvé avec l'ID: " + id));
        return mapToResponse(item);
    }

    @Transactional(readOnly = true)
    public List<CatalogItemResponse> getAllItems() {
        log.debug("Fetching all catalog items");
        return catalogRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CatalogItemResponse> getAvailableItems() {
        log.debug("Fetching all available catalog items");
        return catalogRepository.findByStatus(ItemStatus.AVAILABLE).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CatalogItemResponse> getAvailableItemsPaged(int page, int size) {
        log.debug("Fetching available catalog items - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return catalogRepository.findByStatus(ItemStatus.AVAILABLE, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<CatalogItemResponse> getItemsByCity(String city) {
        log.debug("Fetching catalog items for city: {}", city);
        return catalogRepository.findByCityIgnoreCase(city).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CatalogItemResponse> getItemsByOwner(String ownerId) {
        log.debug("Fetching catalog items for owner: {}", ownerId);
        return catalogRepository.findByOwnerId(ownerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CatalogItemResponse> searchRiads(SearchRequest request) {
        log.debug("Searching riads with criteria: {}", request);

        Sort.Direction direction = "ASC".equalsIgnoreCase(request.getSortDirection())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(direction, request.getSortBy())
        );

        return catalogRepository.searchRiads(
                request.getCity(),
                request.getMinCapacity(),
                request.getMinPrice(),
                request.getMaxPrice(),
                pageable
        ).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<String> getAllAvailableCities() {
        log.debug("Fetching all available cities");
        return catalogRepository.findAllAvailableCities();
    }

    public CatalogItemResponse updateItem(Long id, UpdateItemRequest request) {
        log.info("Updating catalog item with ID: {}", id);

        CatalogItem item = catalogRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Riad non trouvé avec l'ID: " + id));

        if (request.getName() != null) {
            item.setName(request.getName());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        if (request.getCity() != null) {
            item.setCity(request.getCity());
        }
        if (request.getAddress() != null) {
            item.setAddress(request.getAddress());
        }
        if (request.getPricePerNight() != null) {
            item.setPricePerNight(request.getPricePerNight());
        }
        if (request.getCapacity() != null) {
            item.setCapacity(request.getCapacity());
        }
        if (request.getNumberOfRooms() != null) {
            item.setNumberOfRooms(request.getNumberOfRooms());
        }
        if (request.getNumberOfBathrooms() != null) {
            item.setNumberOfBathrooms(request.getNumberOfBathrooms());
        }
        if (request.getAmenities() != null) {
            item.setAmenities(request.getAmenities());
        }
        if (request.getImageUrl() != null) {
            item.setImageUrl(request.getImageUrl());
        }
        if (request.getStatus() != null) {
            item.setStatus(request.getStatus());
        }

        CatalogItem updatedItem = catalogRepository.save(item);
        log.info("Updated catalog item with ID: {}", updatedItem.getId());

        return mapToResponse(updatedItem);
    }

    public CatalogItemResponse updateStatus(Long id, ItemStatus status) {
        log.info("Updating status of catalog item {} to {}", id, status);

        CatalogItem item = catalogRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Riad non trouvé avec l'ID: " + id));

        item.setStatus(status);
        CatalogItem updatedItem = catalogRepository.save(item);

        return mapToResponse(updatedItem);
    }

    public void deleteItem(Long id) {
        log.info("Deleting catalog item with ID: {}", id);

        if (!catalogRepository.existsById(id)) {
            throw new ItemNotFoundException("Riad non trouvé avec l'ID: " + id);
        }

        catalogRepository.deleteById(id);
        log.info("Deleted catalog item with ID: {}", id);
    }

    private CatalogItemResponse mapToResponse(CatalogItem item) {
        return CatalogItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .city(item.getCity())
                .address(item.getAddress())
                .pricePerNight(item.getPricePerNight())
                .capacity(item.getCapacity())
                .numberOfRooms(item.getNumberOfRooms())
                .numberOfBathrooms(item.getNumberOfBathrooms())
                .amenities(item.getAmenities())
                .imageUrl(item.getImageUrl())
                .status(item.getStatus())
                .ownerId(item.getOwnerId())
                .rating(item.getRating())
                .reviewCount(item.getReviewCount())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}

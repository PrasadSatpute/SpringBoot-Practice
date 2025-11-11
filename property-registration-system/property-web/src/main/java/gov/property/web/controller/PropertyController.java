package gov.property.web.controller;

import gov.property.common.ApiResponse;
import gov.property.model.entity.Property;
import gov.property.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<ApiResponse<Property>> createProperty(@Valid @RequestBody Property property) {
        try {
            Property savedProperty = propertyService.createProperty(property);
            return ResponseEntity.ok(ApiResponse.success("Property created successfully", savedProperty));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to create property: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<ApiResponse<Property>> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody Property property) {
        try {
            Property updatedProperty = propertyService.updateProperty(id, property);
            return ResponseEntity.ok(ApiResponse.success("Property updated successfully", updatedProperty));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update property: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Property>> getPropertyById(@PathVariable Long id) {
        return propertyService.getPropertyById(id)
                .map(property -> ResponseEntity.ok(ApiResponse.success("Property found", property)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{propertyNumber}")
    public ResponseEntity<ApiResponse<Property>> getPropertyByNumber(@PathVariable String propertyNumber) {
        return propertyService.getPropertyByNumber(propertyNumber)
                .map(property -> ResponseEntity.ok(ApiResponse.success("Property found", property)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Property>>> getAllProperties() {
        List<Property> properties = propertyService.getAllProperties();
        return ResponseEntity.ok(ApiResponse.success("Properties retrieved successfully", properties));
    }

    @GetMapping("/type/{propertyType}")
    public ResponseEntity<ApiResponse<List<Property>>> getPropertiesByType(@PathVariable String propertyType) {
        List<Property> properties = propertyService.getPropertiesByType(propertyType);
        return ResponseEntity.ok(ApiResponse.success("Properties retrieved successfully", properties));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Property>>> searchProperties(@RequestParam String keyword) {
        List<Property> properties = propertyService.searchProperties(keyword);
        return ResponseEntity.ok(ApiResponse.success("Search completed", properties));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProperty(@PathVariable Long id) {
        try {
            propertyService.deleteProperty(id);
            return ResponseEntity.ok(ApiResponse.success("Property deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to delete property: " + e.getMessage()));
        }
    }
}
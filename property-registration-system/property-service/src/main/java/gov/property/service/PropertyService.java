package gov.property.service;

import gov.property.model.entity.Property;
import gov.property.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public Property createProperty(Property property) {
        if (property.getPropertyNumber() == null || property.getPropertyNumber().isEmpty()) {
            property.setPropertyNumber(generatePropertyNumber());
        }

        if (propertyRepository.existsByPropertyNumber(property.getPropertyNumber())) {
            throw new RuntimeException("Property number already exists");
        }

        return propertyRepository.save(property);
    }

    public Property updateProperty(Long id, Property propertyDetails) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));

        property.setPropertyType(propertyDetails.getPropertyType());
        property.setAddress(propertyDetails.getAddress());
        property.setCity(propertyDetails.getCity());
        property.setState(propertyDetails.getState());
        property.setPinCode(propertyDetails.getPinCode());
        property.setAreaSqft(propertyDetails.getAreaSqft());
        property.setMarketValue(propertyDetails.getMarketValue());
        property.setSurveyNumber(propertyDetails.getSurveyNumber());
        property.setOwnerName(propertyDetails.getOwnerName());
        property.setOwnerAadhar(propertyDetails.getOwnerAadhar());
        property.setOwnerPan(propertyDetails.getOwnerPan());
        property.setOwnerPhone(propertyDetails.getOwnerPhone());
        property.setOwnerEmail(propertyDetails.getOwnerEmail());
        property.setDescription(propertyDetails.getDescription());

        return propertyRepository.save(property);
    }

    @Transactional(readOnly = true)
    public Optional<Property> getPropertyById(Long id) {
        return propertyRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Property> getPropertyByNumber(String propertyNumber) {
        return propertyRepository.findByPropertyNumber(propertyNumber);
    }

    @Transactional(readOnly = true)
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Property> getPropertiesByType(String propertyType) {
        return propertyRepository.findByPropertyType(propertyType);
    }

    @Transactional(readOnly = true)
    public List<Property> getPropertiesByCity(String city) {
        return propertyRepository.findByCity(city);
    }

    @Transactional(readOnly = true)
    public List<Property> searchProperties(String keyword) {
        return propertyRepository.searchProperties(keyword);
    }

    public void deleteProperty(Long id) {
        if (!propertyRepository.existsById(id)) {
            throw new RuntimeException("Property not found with id: " + id);
        }
        propertyRepository.deleteById(id);
    }

    private String generatePropertyNumber() {
        return "PROP-" + System.currentTimeMillis() + "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
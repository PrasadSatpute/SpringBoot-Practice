package com.property.registration.service;

import com.property.registration.entity.ULB;
import com.property.registration.entity.ULBLevel;
import com.property.registration.repository.ULBRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ULBService {

    private final ULBRepository ulbRepository;

    public ULB createULB(ULB ulb) {
        if (ulbRepository.existsByUlbCode(ulb.getUlbCode())) {
            throw new RuntimeException("ULB Code already exists");
        }
        ulb.setActive(true);
        return ulbRepository.save(ulb);
    }

    public ULB updateULB(Long ulbId, ULB updatedULB) {
        ULB existingULB = ulbRepository.findById(ulbId)
                .orElseThrow(() -> new RuntimeException("ULB not found"));

        existingULB.setUlbName(updatedULB.getUlbName());
        existingULB.setCity(updatedULB.getCity());
        existingULB.setZone(updatedULB.getZone());
        existingULB.setWard(updatedULB.getWard());
        existingULB.setUlbLevel(updatedULB.getUlbLevel());
        existingULB.setAddress(updatedULB.getAddress());
        existingULB.setContactNumber(updatedULB.getContactNumber());
        existingULB.setContactEmail(updatedULB.getContactEmail());

        return ulbRepository.save(existingULB);
    }

    public void deleteULB(Long ulbId) {
        ULB ulb = ulbRepository.findById(ulbId)
                .orElseThrow(() -> new RuntimeException("ULB not found"));
        ulb.setActive(false);
        ulbRepository.save(ulb);
    }

    public ULB getULBById(Long ulbId) {
        return ulbRepository.findById(ulbId)
                .orElseThrow(() -> new RuntimeException("ULB not found"));
    }

    public ULB getULBByCode(String ulbCode) {
        return ulbRepository.findByUlbCode(ulbCode)
                .orElseThrow(() -> new RuntimeException("ULB not found"));
    }

    public List<ULB> getAllULBs() {
        return ulbRepository.findAll();
    }

    public List<ULB> getULBsByCity(String city) {
        return ulbRepository.findByCity(city);
    }

    public List<ULB> getULBsByCityAndZone(String city, String zone) {
        return ulbRepository.findByCityAndZone(city, zone);
    }

    public ULB findULBByLocation(String city, String zone, String ward) {
        List<ULB> ulbs = ulbRepository.findByCityAndZoneAndWard(city, zone, ward);
        if (ulbs.isEmpty()) {
            throw new RuntimeException("No ULB found for the specified location");
        }
        return ulbs.get(0);
    }

    public List<ULB> getULBsByLevel(ULBLevel level) {
        return ulbRepository.findByUlbLevel(level);
    }
}
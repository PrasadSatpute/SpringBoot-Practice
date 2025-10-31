package com.example.mahasbr.controller;

import com.example.mahasbr.dto.VisitorCountDTO;
import com.example.mahasbr.service.VisitorCountService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/visitor")
//@CrossOrigin(origins = "*")
public class VisitorCountController {

    private final VisitorCountService visitorCountService;

    public VisitorCountController(VisitorCountService visitorCountService) {
        this.visitorCountService = visitorCountService;
    }

    @PostMapping(value = "/increment", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VisitorCountDTO> incrementVisitorCount() {
        VisitorCountDTO result = visitorCountService.incrementVisitorCount();
        System.out.println("Count");
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VisitorCountDTO> getVisitorCount() {
        VisitorCountDTO result = visitorCountService.getVisitorCount();
        return ResponseEntity.ok(result);
    }
}

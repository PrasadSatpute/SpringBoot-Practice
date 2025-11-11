package com.example.mahasbr.service;

import com.example.mahasbr.dto.VisitorCountDTO;
import com.example.mahasbr.entity.VisitorCount;
import com.example.mahasbr.repository.VisitorCountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
public class VisitorCountService {

    private final VisitorCountRepository visitorCountRepository;

    public VisitorCountService(VisitorCountRepository visitorCountRepository) {
        this.visitorCountRepository = visitorCountRepository;
    }

    @Transactional
    public VisitorCountDTO incrementVisitorCount() {
        LocalDate today = LocalDate.now();

        // Try to find today's record
        VisitorCount visitorCount = visitorCountRepository.findByDate(today)
                .orElseGet(() -> {
                    // If today's record doesn't exist, create new one
                    VisitorCount newCount = new VisitorCount();
                    newCount.setDate(today);
                    newCount.setTodayCount(0L);

                    // Get the last total count from previous day
                    Long lastTotalCount = visitorCountRepository.findTopByOrderByDateDesc()
                            .map(VisitorCount::getTotalCount)
                            .orElse(0L);

                    newCount.setTotalCount(lastTotalCount);
                    return newCount;
                });

        // Increment counts
        visitorCount.setTodayCount(visitorCount.getTodayCount() + 1);
        visitorCount.setTotalCount(visitorCount.getTotalCount() + 1);

        // Save and return
        VisitorCount saved = visitorCountRepository.save(visitorCount);
        return convertToDTO(saved);
    }

    @Transactional(readOnly = true)
    public VisitorCountDTO getVisitorCount() {
        LocalDate today = LocalDate.now();

        // Try to find today's record
        VisitorCount visitorCount = visitorCountRepository.findByDate(today)
                .orElseGet(() -> {
                    // If no record for today, get the last available count
                    return visitorCountRepository.findTopByOrderByDateDesc()
                            .map(lastCount -> {
                                VisitorCount todayCount = new VisitorCount();
                                todayCount.setDate(today);
                                todayCount.setTodayCount(0L);
                                todayCount.setTotalCount(lastCount.getTotalCount());
                                return todayCount;
                            })
                            .orElseGet(() -> {
                                // No records at all, return initial values
                                VisitorCount initialCount = new VisitorCount();
                                initialCount.setDate(today);
                                initialCount.setTodayCount(0L);
                                initialCount.setTotalCount(0L);
                                return initialCount;
                            });
                });

        return convertToDTO(visitorCount);
    }

    private VisitorCountDTO convertToDTO(VisitorCount visitorCount) {
        return new VisitorCountDTO(
                visitorCount.getTotalCount(),
                visitorCount.getTodayCount(),
                visitorCount.getDate()
        );
    }
}
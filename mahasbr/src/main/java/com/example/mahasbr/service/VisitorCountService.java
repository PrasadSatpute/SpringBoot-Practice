package com.example.mahasbr.service;

import com.example.mahasbr.dto.VisitorCountDTO;
import com.example.mahasbr.entity.VisitorCount;
import com.example.mahasbr.repository.VisitorCountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
public class VisitorCountService {

    private final VisitorCountRepository visitorCountRepository;

    public VisitorCountService(VisitorCountRepository visitorCountRepository) {
        this.visitorCountRepository = visitorCountRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public VisitorCountDTO incrementVisitorCount() {
        LocalDate today = LocalDate.now();
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                // Try to find today's record with pessimistic write lock
                VisitorCount visitorCount = visitorCountRepository
                        .findByDateWithLock(today)
                        .orElseGet(() -> createNewDayRecord(today));

                // Increment counts
                visitorCount.setTodayCount(visitorCount.getTodayCount() + 1);
                visitorCount.setTotalCount(visitorCount.getTotalCount() + 1);

                // Save and return
                VisitorCount saved = visitorCountRepository.save(visitorCount);

                return convertToDTO(saved);

            } catch (Exception e) {
                retryCount++;


                if (retryCount >= maxRetries) {

                    throw new RuntimeException("Failed to increment visitor count", e);
                }

                try {
                    Thread.sleep(100 * retryCount); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted during retry", ie);
                }
            }
        }

        throw new RuntimeException("Failed to increment visitor count");
    }

    private VisitorCount createNewDayRecord(LocalDate today) {
        VisitorCount newCount = new VisitorCount();
        newCount.setDate(today);
        newCount.setTodayCount(0L);

        // Get the last total count from previous day
        Long lastTotalCount = visitorCountRepository.findTopByOrderByDateDesc()
                .map(VisitorCount::getTotalCount)
                .orElse(0L);

        newCount.setTotalCount(lastTotalCount);
        return visitorCountRepository.save(newCount);
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
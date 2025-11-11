package com.pmt.ProjectManagement.controller;

import com.pmt.ProjectManagement.enums.ProjectStatus;
import com.pmt.ProjectManagement.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class CalendarController {

    @GetMapping("/calendar")
    public String showCalendar(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        LocalDate currentDate = LocalDate.now();
        model.addAttribute("currentDate", currentDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        model.addAttribute("currentMonth", currentDate.getMonthValue() - 1); // Months are 0-indexed in JavaScript
        model.addAttribute("currentYear", currentDate.getYear());

        model.addAttribute("currentUser", userDetails.getUser());
        return "calendar";
    }
}

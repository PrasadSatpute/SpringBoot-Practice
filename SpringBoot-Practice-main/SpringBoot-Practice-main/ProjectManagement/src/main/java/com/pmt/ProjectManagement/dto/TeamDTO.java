package com.pmt.ProjectManagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO {

    private Long id;

    @NotBlank(message = "Team name is required")
    private String name;

    private String description;

    @NotNull(message = "Team lead is required")
    private Long teamLeadId;

    private Set<Long> memberIds;

    // Add getter and setter
    private List<String> memberNames;  // Add this to TeamDTO

    private String teamLeadName;

    public String getTeamLeadName() {
        return teamLeadName;
    }


    private String createdAt;
    private String updatedAt;
}

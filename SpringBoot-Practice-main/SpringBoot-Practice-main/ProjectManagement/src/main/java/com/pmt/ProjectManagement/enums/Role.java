package com.pmt.ProjectManagement.enums;

public enum Role {
    ADMIN("Admin"),
    MANAGER("Manager"),
    CEO("CEO"),
    PROJECT_LEAD("Project Lead"),
    TEAM_LEAD("Team Lead"),
    BUSINESS_ANALYST("Business Analyst"),
    DEVELOPER("Developer"),
    TESTER("Tester"),
    QA_LEAD("QA Lead"),
    SCRUM_MASTER("Scrum Master"),
    PRODUCT_OWNER("Product Owner");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
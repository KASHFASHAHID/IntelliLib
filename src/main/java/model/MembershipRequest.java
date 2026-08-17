package model;

public class MembershipRequest {

    private int requestId;
    private String fullName;
    private String brainwareId;
    private String roleRequested;
    private String department;
    private String email;
    private String phone;
    private String status;

    // New fields
    private String university;
    private String courseOrDesignation;
    private Integer semester;
    private String reason;

    public MembershipRequest(int requestId,
                             String fullName,
                             String brainwareId,
                             String roleRequested,
                             String department,
                             String email,
                             String phone,
                             String status,
                             String university,
                             String courseOrDesignation,
                             Integer semester,
                             String reason) {

        this.requestId = requestId;
        this.fullName = fullName;
        this.brainwareId = brainwareId;
        this.roleRequested = roleRequested;
        this.department = department;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.university = university;
        this.courseOrDesignation = courseOrDesignation;
        this.semester = semester;
        this.reason = reason;
    }

    public int getRequestId() {
        return requestId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getBrainwareId() {
        return brainwareId;
    }

    public String getRoleRequested() {
        return roleRequested;
    }

    public String getDepartment() {
        return department;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getStatus() {
        return status;
    }

    public String getUniversity() {
        return university;
    }

    public String getCourseOrDesignation() {
        return courseOrDesignation;
    }

    public Integer getSemester() {
        return semester;
    }

    public String getReason() {
        return reason;
    }
}
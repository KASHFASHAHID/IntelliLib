package model;

public class Profile {

    private String userId;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String university;
    private String department;
    private String accountStatus;

    public Profile(
            String userId,
            String name,
            String email,
            String phone,
            String role,
            String university,
            String department,
            String accountStatus) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.university = university;
        this.department = department;
        this.accountStatus = accountStatus;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }

    public String getUniversity() {
        return university;
    }

    public String getDepartment() {
        return department;
    }

    public String getAccountStatus() {
        return accountStatus;
    }
}
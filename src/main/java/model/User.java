package model;

public class User {

    private String userId;
    private String password;
    private String name;
    private String email;
    private Role role;
    private String accountStatus;

    public User() {
    }

    /*
     * Keeps existing repository and service code working.
     * This constructor is currently used for users already confirmed ACTIVE.
     */
    public User(
            String userId,
            String password,
            String name,
            String email,
            Role role
    ) {

        this(
                userId,
                password,
                name,
                email,
                role,
                "ACTIVE"
        );
    }

    /*
     * Use this constructor when the database query also loads
     * the real account status.
     */
    public User(
            String userId,
            String password,
            String name,
            String email,
            Role role,
            String accountStatus
    ) {

        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = role;
        this.accountStatus = accountStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(
            String userId
    ) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(
            String password
    ) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(
            String name
    ) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(
            String email
    ) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(
            Role role
    ) {
        this.role = role;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(
            String accountStatus
    ) {
        this.accountStatus = accountStatus;
    }

    public boolean isSuspended() {

        return "SUSPENDED".equalsIgnoreCase(
                accountStatus
        );
    }

    public boolean isActive() {

        return "ACTIVE".equalsIgnoreCase(
                accountStatus
        );
    }
}
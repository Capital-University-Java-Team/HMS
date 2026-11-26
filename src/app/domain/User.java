package app.domain;

public abstract class User {
    private final String id; 
    private String name;
    private String phone;
    private String password;
    private final Role role;

    protected User(String id, String name, String phone, String password, Role role) {
        this.id       = DomainException.requireNonBlank(id, "User ID is required.");
        this.role     = DomainException.requireNonNull(role, "Role is required.");
        this.name     = DomainException.requireNonBlank(name, "Name is required.");
        this.password = DomainException.requireNonBlank(password, "Password is required.");
        this.phone    = normalizeOptionalPhone(phone);
    }

    public void updateProfile(String newName, String newPhone, String newPassword) {
        String validNewName = DomainException.requireNonBlank(newName, "Name is required");
        String validNewPassword = DomainException.requireNonBlank(newPassword, "Password is required");
        this.name = validNewName;
        this.password = validNewPassword;
        this.phone    = normalizeOptionalPhone(newPhone);
    }

    private static String normalizeOptionalPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        return phone.trim();
    }

    public boolean checkPassword(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
}


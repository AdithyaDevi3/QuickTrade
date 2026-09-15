package quicktrade.com.entity;

/** Shared request body for /api/auth/register and /api/auth/login. */
public class AuthRequestDTO {

    private String email;
    private String password;

    public AuthRequestDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

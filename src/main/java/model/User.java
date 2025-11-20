package model;

/**
 * Model untuk representasi data user
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String nama;
    private String telp;
    private String role;
    private boolean status;
    
    public User() {
    }
    
    public User(int id, String username, String password, String email, 
                String nama, String telp, String role, boolean status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.nama = nama;
        this.telp = telp;
        this.role = role;
        this.status = status;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getNama() {
        return nama;
    }
    
    public void setNama(String nama) {
        this.nama = nama;
    }
    
    public String getTelp() {
        return telp;
    }
    
    public void setTelp(String telp) {
        this.telp = telp;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public boolean isStatus() {
        return status;
    }
    
    public void setStatus(boolean status) {
        this.status = status;
    }
}


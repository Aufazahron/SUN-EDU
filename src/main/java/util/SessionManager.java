package util;

import model.User;

/**
 * Utility class untuk mengelola session user yang sedang login
 */
public class SessionManager {
    private static User currentUser;
    
    /**
     * Menyimpan user yang sedang login
     * @param user User object yang akan disimpan
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    /**
     * Mendapatkan user yang sedang login
     * @return User object jika ada, null jika tidak ada
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Menghapus session user (logout)
     */
    public static void clearSession() {
        currentUser = null;
    }
    
    /**
     * Mengecek apakah ada user yang sedang login
     * @return true jika ada user, false jika tidak ada
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}


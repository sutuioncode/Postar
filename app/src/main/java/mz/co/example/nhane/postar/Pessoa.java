package mz.co.example.nhane.postar;

import android.net.Uri;

/**
 * Created by sutuioncode on 5/22/2018.
 */

public class Pessoa {
    private String firebaserId;
    private String username;
    private String email;
    private String password;
    private Uri foto;

    public Pessoa() {
    }

    public Pessoa(String firebaserId, String username, String email, String password) {
        this.firebaserId = firebaserId;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getFirebaserId() {
        return firebaserId;
    }

    public void setFirebaserId(String firebaserId) {
        this.firebaserId = firebaserId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Uri getFoto() {
        return foto;
    }

    public void setFoto(Uri foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "Pessoa{" +
                "firebaserId='" + firebaserId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

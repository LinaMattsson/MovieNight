package testMovieNight.entities;

import javax.persistence.*;

@Entity
@Table (name="user")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer i;
    private String mail;
    private String name;
    private String accessToken;
    private String refreshToken;
    private Long expirationDate;

    public User(){

    }
    public User(String mail, String name, String accessToken, String refreshToken, Long expirationDate){
        this.mail = mail;
        this.name = name;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expirationDate = expirationDate;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Long expirationDate) {
        this.expirationDate = expirationDate;
    }
}

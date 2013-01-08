package bd;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.*;
import java.io.Serializable;

public class User implements Serializable {

    private int userID;
    private String name;
    private String username;
    private String pass;
    private Date birthday;
    private Boolean gender;
    private String email;
    private String job;
    private String nationality;
    private String citizenship;
    private String city;
    private String interests;
    private Boolean privacy;
    private Boolean active;

    public User() {
    }

    public User(String name, String username, String pass, Boolean gender, String email, String job, String nationality, String citizenship, String city, String interests, Boolean privacy) {
        this.name = name;
        this.username = username;
        this.pass = pass;
        this.gender = gender;
        this.email = email;
        this.job = job;
        this.nationality = nationality;
        this.citizenship = citizenship;
        this.city = city;
        this.interests = interests;
        this.privacy = privacy;
        this.active = true;
        this.birthday = new Date();
    }
    public User(String name, String username, String pass, String email, String job, String nationality, String citizenship, String city, String interests, Boolean privacy) {
        this.name = name;
        this.username = username;
        this.pass = pass;
        this.email = email;
        this.job = job;
        this.nationality = nationality;
        this.citizenship = citizenship;
        this.city = city;
        this.interests = interests;
        this.privacy = privacy;
        this.active = true;
        this.birthday = new Date();
    }
    
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date bday) {
        this.birthday = bday;
    }

    public Boolean isGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public Boolean isPrivacy() {
        return privacy;
    }

    public void setPrivacy(Boolean privacy) {
        this.privacy = privacy;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}

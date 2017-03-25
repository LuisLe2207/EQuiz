package com.example.luisle.equiz.Model;

/**
 * Created by LuisLe on 2/4/2017.
 */

public class User {

    private String ID;
    private String email;
    private String fullName;
    private String profilePicture;
    private String mobile;

    // Get and Set

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    // Contructor

    public User() {
        // Firebase required
    }

    public User(String ID, String email, String fullName, String profilePicture, String mobile) {
        this.ID = ID;
        this.email = email;
        this.fullName = fullName;
        this.profilePicture = profilePicture;
        this.mobile = mobile;
    }

}

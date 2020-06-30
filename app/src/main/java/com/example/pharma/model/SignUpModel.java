package com.example.pharma.model;

import java.io.Serializable;

public class SignUpModel  implements Serializable {
    private String Name;
    public String pwd;
    private String Email;
    private String Mobile;
    private String user_img;

    public SignUpModel(String name, String email, String mobile,String img) {
        Name = name;
        Email = email;
        Mobile = mobile;
        user_img=img;
    }

    public SignUpModel() {

    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }
}

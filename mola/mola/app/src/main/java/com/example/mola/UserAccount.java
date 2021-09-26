package com.example.mola;

/*
    사용자정보 모델클래스
 */
public class UserAccount
{

    private String idToken;//firebase 고유 토큰정보
    private String emailId; //이메일 아이디
    private String password;
    private String name;



    public UserAccount() { }

    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public void setEmailId(String emailId) {this.emailId = emailId; }

    public void setPassword(String password) { this.password = password; }

    public String getEmailId() { return emailId; }

    public String getPassword() { return password; }
}

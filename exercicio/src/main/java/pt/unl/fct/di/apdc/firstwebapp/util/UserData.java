package pt.unl.fct.di.apdc.firstwebapp.util;

public class UserData {

    public String username;
    public String email;
    public String name;
    public String role;
    public String accountState;
    public String profile;
    public String phoneNumber;
    public String mobilePhone;
    public String occupation;
    public String workplace;
    public String address;
    public String compAddress;
    public String nif;

    public UserData() {}
    
    public UserData(String username, String email, String name) {
    	this.username = username;
        this.email = email;
        this.name = name;
    }

    public UserData(String username, String email, String name, String role, String accountState, String profile, String phoneNumber,
            String mobilePhone, String occupation, String workplace, String address, String compAddress, String nif) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.role = role;
        this.accountState = accountState;
        this.profile = profile;
        this.phoneNumber = phoneNumber;
        this.mobilePhone = mobilePhone;
        this.occupation = occupation;
        this.workplace = workplace;
        this.address = address;
        this.compAddress = compAddress;
        this.nif = nif;
    }

}

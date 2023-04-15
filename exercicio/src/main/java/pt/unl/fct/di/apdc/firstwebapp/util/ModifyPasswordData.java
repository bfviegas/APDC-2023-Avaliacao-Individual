package pt.unl.fct.di.apdc.firstwebapp.util;

public class ModifyPasswordData {
    public String currentPassword;
    public String newPassword;
    public String newPasswordConfirmation;

    public ModifyPasswordData() {
    }

    public ModifyPasswordData(String currentPassword, String newPassword, String newPasswordConfirmation) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.newPasswordConfirmation = newPasswordConfirmation;
    }
}
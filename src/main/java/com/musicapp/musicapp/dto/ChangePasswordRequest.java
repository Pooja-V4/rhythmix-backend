package com.musicapp.musicapp.dto;

public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;

    public ChangePasswordRequest() {}
    public String getCurrentPassword() { return currentPassword; }
    public String getNewPassword() { return newPassword; }
    public void setCurrentPassword(String p) { this.currentPassword = p; }
    public void setNewPassword(String p) { this.newPassword = p; }
}
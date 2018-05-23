package com.justforfun.keywordsalert.entity;

import java.util.Set;

public class Setting {
    private boolean pushOption;
    private boolean emailOption;
    private String email;
    private int timerMillisecond;

    private Set<String> keywords;
    private Set<String> websites;

    public Setting() {
    }


    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords;
    }

    public Set<String> getWebsites() {
        return websites;
    }

    public void setWebsites(Set<String> websites) {
        this.websites = websites;
    }

    public boolean isPushOption() {
        return pushOption;
    }

    public boolean isEmailOption() {
        return emailOption;
    }

    public void setPushOption(boolean pushOption) {
        this.pushOption = pushOption;
    }


    public void setEmailOption(boolean emailOption) {
        this.emailOption = emailOption;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTimerMillisecond() {
        return timerMillisecond;
    }

    public void setTimerMillisecond(int timerMillisecond) {
        this.timerMillisecond = timerMillisecond;
    }

    @Override
    public String toString() {
        return "Setting{" +
                "pushOption=" + pushOption +
                ", emailOption=" + emailOption +
                ", email='" + email + '\'' +
                ", timerMillisecond=" + timerMillisecond +
                ", keywords=" + keywords +
                ", websites=" + websites +
                '}';
    }
}

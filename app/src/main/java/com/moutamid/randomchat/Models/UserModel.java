package com.moutamid.randomchat.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {
    public String name, gender, email, uid,language;

    public String profile_url;
//    public int followers_count, following_count;

    public boolean is_vip;

    public UserModel() {
    }

    protected UserModel(Parcel in) {
        name = in.readString();
        gender = in.readString();
        email = in.readString();
        uid = in.readString();
        language = in.readString();
        profile_url = in.readString();
        is_vip = in.readByte() != 0;
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public boolean isIs_vip() {
        return is_vip;
    }

    public void setIs_vip(boolean is_vip) {
        this.is_vip = is_vip;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(gender);
        parcel.writeString(email);
        parcel.writeString(uid);
        parcel.writeString(language);
        parcel.writeString(profile_url);
        parcel.writeByte((byte) (is_vip ? 1 : 0));
    }
}

package com.kmmaruf.attendancemanagementsystem.nativeclasses.model;

public class User {
    public int uid;
    public String name;
    public int privilege;
    public String password;
    public String groupId;
    public String userId;
    public long card;

    public User(int uid, String name, int privilege, String password, String groupId, String userId, long card) {
        this.uid = uid;
        this.name = name;
        this.privilege = privilege;
        this.password = password;
        this.groupId = groupId;
        this.userId = userId;
        this.card = card;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        User other = (User) obj;
        return uid == other.uid && privilege == other.privilege && card == other.card && name.equals(other.name) && password.equals(other.password) && groupId.equals(other.groupId) && userId.equals(other.userId);
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(uid);
        result = 31 * result + name.hashCode();
        result = 31 * result + Integer.hashCode(privilege);
        result = 31 * result + password.hashCode();
        result = 31 * result + groupId.hashCode();
        result = 31 * result + userId.hashCode();
        result = 31 * result + Long.hashCode(card);
        return result;
    }

}

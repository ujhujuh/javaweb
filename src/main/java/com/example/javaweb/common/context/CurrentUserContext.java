package com.example.javaweb.common.context;

import com.example.javaweb.entity.SysUser;

public class CurrentUserContext {

    private static final ThreadLocal<SysUser> USER_HOLDER = new ThreadLocal<>();

    private CurrentUserContext() {
    }

    public static void setUser(SysUser user) {
        USER_HOLDER.set(user);
    }

    public static SysUser getUser() {
        return USER_HOLDER.get();
    }

    public static Long getUserId() {
        SysUser user = USER_HOLDER.get();
        return user == null ? null : user.getId();
    }

    public static String getUsername() {
        SysUser user = USER_HOLDER.get();
        return user == null ? null : user.getUsername();
    }

    public static void clear() {
        USER_HOLDER.remove();
    }
}

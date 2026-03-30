package com.example.javaweb.common.context;

import com.example.javaweb.entity.SysUser;

public class CurrentUserContext {

    private static final ThreadLocal<SysUser> USER_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> LANGUAGE_HOLDER = new ThreadLocal<>();

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

    public static void setLanguage(String language) {
        LANGUAGE_HOLDER.set(language);
    }

    public static String getLanguage() {
        // 优先使用请求头设置的语言，其次使用用户数据库中设置的语言
        String language = LANGUAGE_HOLDER.get();
        if (language != null) {
            return language;
        }

        SysUser user = USER_HOLDER.get();
        if (user != null && user.getLanguage() != null) {
            return user.getLanguage();
        }

        return "zh"; // 默认中文
    }

    public static void clear() {
        USER_HOLDER.remove();
        LANGUAGE_HOLDER.remove();
    }
}

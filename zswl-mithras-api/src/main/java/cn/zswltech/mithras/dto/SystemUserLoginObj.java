package cn.zswltech.mithras.dto;

import lombok.Data;

import java.util.List;

/**
 * @author zhouning
 * @date 2025/04/28
 * @description
 */
@Data
public class SystemUserLoginObj {
    private int code;

    private String message;

    private boolean success;

    private Data data;

    @lombok.Data
    public static class Data
    {
        private License license;

        private boolean hasSolution;

        private String tdToken;

        private String csrfToken;

        private long expiration;

        private String indexPath;

        private String userId;

        private User user;
    }

    @lombok.Data
    public static class License
    {
        private String license;

        private int appNum;

        private int code;

        private boolean success;

        private int leftDay;

        private long expiration;

        private String appCode;

        private int productNum;

    }

    @lombok.Data
    public static class Roles
    {
        private String uuid;

        private long gmtCreate;

        private long gmtModified;

        private String code;

        private String name;

        private String type;

        private String orgUuid;

        private String enName;

    }

    @lombok.Data
    public static class User
    {
        private String uuid;

        private long gmtCreate;

        private long gmtModified;

        private String account;

        private String userName;

        private String orgUuid;

        private String roleUuids;

        private String avatar;

        private long expiration;

        private long gender;

        private String appName;

        private long status;

        private String updateBy;

        private String lang;

        private String theme;

        private String layout;

        private long simplified;

        private String tokenMD5;

        private long tryTime;

        private long tryDate;

        private long updatePwdTime;

        private String firstLogin;

        private boolean updatePwdStatus;

        private List<Roles> roles;


    }


}






package cn.zswltech.mithras.foundation.thirdparty;

/**
 * 三方接口调用方式枚举
 *
 * @author wang
 * @date 2022/4/11 7:10 PM
 */
public enum RequestModeEnum {

    POST(1L, "post"),
    GET(2L, "get"),
    GETANDUPLOAD(3L, "getAndLoad"),
    POST_FORM(4L, "postForm"),
    PUT(5L, "put"),
    PUT_UPLOAD(6L,"putUpLoad"),
    DELETE(7L,"delete"),
    FROM_DATA(8L, "form-data"),
    ;

    /**
     * ID
     */
    public final Long id;
    /**
     * 请求方式
     */
    public final String requestMode;

    RequestModeEnum(Long id, String requestMode) {
        this.id = id;
        this.requestMode = requestMode;
    }
}

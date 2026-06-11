package cn.zswltech.mithras.foundation.enums;

/**
 * @Description:
 * @Author:wangshengan
 * @Date:2019/04/015
 */
public enum ContentTypeEnum {
    json("application/json"),
    x_www_form_urlencoded("application/x-www-form-urlencoded"),
    text_xml("text/xml"),
    from_data("multipart/form-data"),
    ;

    private String value;

    ContentTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
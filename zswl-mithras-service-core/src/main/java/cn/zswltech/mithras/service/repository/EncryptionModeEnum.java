package cn.zswltech.mithras.service.repository;

/**
 * 三方接口加密方式枚举
 *
 * @author wang
 * @date 2022/4/11 7:10 PM
 */
public enum EncryptionModeEnum {

    NONE(0L, "none"),
    MD_5(1L, "MD5"),
    DES(2L,"DES")

    ;

    EncryptionModeEnum(Long id, String name){
        this.id = id;
        this.name = name;
    }

    /**
     * ID
     */
    public final Long id;

    /**
     * 加密名称
     */
    public final String name;
}

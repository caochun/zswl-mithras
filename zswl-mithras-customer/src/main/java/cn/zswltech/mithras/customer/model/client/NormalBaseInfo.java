package cn.zswltech.mithras.customer.model.client;

import cn.zswltech.mithras.foundation.persistence.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author MyBatisPlusGenerater
 * @since 2022-06-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("normal_base_info")
public class NormalBaseInfo extends ClientBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 证件类型
     */
    @TableField("cert_type")
    private String certType;

    /**
     * 证件号码
     */
    @TableField("cert_number")
    private String certNumber;

    /**
     * 性别
     */
    @TableField("gender")
    private String gender;

    /**
     * 婚姻情况
     */
    @TableField("marriage_type")
    private String marriageType;

    /**
     * 国家
     */
    @TableField("country")
    private String country;

    /**
     * 年龄
     */
    @TableField("age")
    @IncludeNull
    private Integer age;

    /**
     * 手机号
     */
    @TableField("mobile_number")
    @IncludeNull
    private String mobileNumber;

    /**
     * 家庭地址
     */
    @TableField("home_address")
    @IncludeNull
    private String homeAddress;

    /**
     * 邮箱
     */
    @TableField("mail")
    @IncludeNull
    private String mail;

    /**
     * 客户编号
     */
    @TableField("client_code")
    @IncludeNull
    private String clientCode;

    //非数据库字段
    @TableField(exist = false)
    private String clientName;


}

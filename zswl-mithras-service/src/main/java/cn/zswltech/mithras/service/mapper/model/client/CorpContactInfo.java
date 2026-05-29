package cn.zswltech.mithras.service.mapper.model.client;

import cn.zswltech.mithras.common.annotation.IncludeNull;
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
 * @since 2022-06-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("corp_contact_info")
public class CorpContactInfo extends ClientBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否主联系人
     */
    @TableField("main")
    @IncludeNull
    private Boolean main;

    /**
     * 职务
     */
    @TableField("position")
//    @IncludeNull
    private String position;

    /**
     * 性别
     */
    @TableField("gender")
    @IncludeNull
    private String gender;

    /**
     * 姓名
     */
    @TableField("name")
//    @IncludeNull
    private String name;

    /**
     * 电话
     */
    @TableField("telephone")
//    @IncludeNull
    private String telephone;

    /**
     * 座机
     */
    @TableField("landline_telephone")
    @IncludeNull
    private String landlineTelephone;

    /**
     * 邮箱
     */
    @TableField("mail")
    @IncludeNull
    private String mail;

    /**
     * 证件类型
     */
    @TableField("cert_type")
    @IncludeNull
    private String certType;

    /**
     * 证件号码
     */
    @TableField("cert_number")
    @IncludeNull
    private String certNumber;

    @TableField(value = "user_id")
    private Long userId;

}

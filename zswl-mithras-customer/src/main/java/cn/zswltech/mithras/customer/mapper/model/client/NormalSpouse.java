package cn.zswltech.mithras.customer.mapper.model.client;

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
 * @since 2022-06-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("normal_spouse")
public class NormalSpouse extends ClientBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配偶姓名
     */
    @TableField("spouse_name")
    private String spouseName;

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


}

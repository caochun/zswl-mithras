package cn.zswltech.mithras.customer.externalcustomer.infrastructure.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author shaokang
 * @description 外部客户名单表
 * @date 2026-01-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("external_customer")
public class ExternalCustomer {

    @TableId(type = IdType.AUTO)
    private Long id;

    // 客户名称
    @TableField("client_name")
    private String clientName;

    // 统一社会信用代码
    @TableField("usc_code")
    private String uscCode;

    // 是否使用: 0-没有,1-已使用
    @TableField("is_use")
    private String isUse;

}

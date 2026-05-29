package cn.zswltech.mithras.service.mapper.model.client;

import cn.zswltech.mithras.common.annotation.IncludeNull;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * @description 中登网
 * @author yeqing
 * @date 2022-06-21
 */
@Data
public class ZhongdengInfo extends TycBaseModel {

    @TableId(type = IdType.AUTO)
    /**
    * 主键
    */
    private Long id;

    /**
     * 客户id
     */
    @TableField("client_id")
    private Long clientId;

    /**
    * 交易业务类型
    */
    @TableField("trade_business_type")
    @IncludeNull
    private String tradeBusinessType;

    /**
    * 授信机构
    */
    @TableField("credit_org")
    @IncludeNull
    private String creditOrg;

    /**
    * 金额
    */
    @TableField("amount")
    @IncludeNull
    private Long amount;

    /**
    * 登记日期
    */
    @TableField("reg_date")
    @IncludeNull
    private LocalDateTime regDate;

    /**
    * 登记到期日
    */
    @TableField("reg_expire_date")
    @IncludeNull
    private LocalDateTime regExpireDate;

    /**
    * 期限（年） create_time datetime default current_timestamp
    */
    @TableField("term")
    @IncludeNull
    private Integer term;
}
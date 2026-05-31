package cn.zswltech.mithras.service.mapper.model.fund;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description fund_credit
 * @date 2022-12-13
 */
@Data
public class FundCredit extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 授信机构
     */
    @TableField("organization_id")
    private Long organizationId;

    /**
     * 总授信额度
     */
    @TableField("total_credit_limit")
    private Long totalCreditLimit;

    /**
     * 授信编号
     */
    @TableField("credit_code")
    private String creditCode;

    /**
     * 增信方式
     */
    @TableField("enhance_credit_method")
    private String enhanceCreditMethod;

    /**
     * 担保方 ??好像没有使用
     */
    /*@TableField("guarantor")
    private String guarantor;*/

    /**
     * 额度是否可循环
     */
    @TableField("recyclable")
    private Integer recyclable;

    /**
     * 资金用途
     */
    @TableField("fund_usage")
    private String fundUsage;

    /**
     * 授信生效时间
     */
    @TableField("effective_date_from")
    private LocalDate effectiveDateFrom;

    /**
     * 授信生效时间
     */
    @TableField("effective_date_to")
    private LocalDate effectiveDateTo;

    /**
     * 资金经理
     */
    @TableField("fund_manager")
    private Long fundManager;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 是否有效
     */
    @TableField("effective")
    private Boolean effective;


}

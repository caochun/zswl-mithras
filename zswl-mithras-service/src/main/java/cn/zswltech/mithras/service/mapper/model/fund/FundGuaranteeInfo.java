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
 * @description fund_guarantee_info
 * @date 2022-12-13
 */
@Data
public class FundGuaranteeInfo extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 担保机构id
     */
    @TableField("agency_id")
    private Long agencyId;

    /**
     * 担保编号
     */
    @TableField("guarantee_code")
    private String guaranteeCode;

    /**
     * 总担保额度
     */
    @TableField("total_guarantee_limit")
    private Long totalGuaranteeLimit;

    /**
     * 已使用额度
     */
    @TableField("used_guarantee_limit")
    private Long usedGuaranteeLimit;

    /**
     * 担保生效时间from
     */
    @TableField("effective_time_from")
    private LocalDate effectiveTimeFrom;

    /**
     * 担保生效时间to
     */
    @TableField("effective_time_to")
    private LocalDate effectiveTimeTo;

    /**
     * 额度是否可循环 0否1是
     */
    @TableField("recyclable")
    private Integer recyclable;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 是否生效
     */
    @TableField("effective")
    private Integer effective;

}

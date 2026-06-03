package cn.zswltech.mithras.financeprojectdistribution.mapper.model;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author lllin
 * @date2025/12/19
 * @description 财务-项目分配表（新）-基本信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("finance_project_distribution_base_info")
public class FinanceProjectDistributionBaseInfo extends BaseModel implements IEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 项目分配id
     */
    @TableField(value = "project_distribution_id")
    private Long projectDistributionId;

    /**
     * 合同编号
     */
    @TableField(value = "contract_code")
    private String contractCode;

    /**
     * 项目名称
     */
    @TableField(value = "proj_name")
    private String projName;
    /**
     * 项目编号
     */
    @TableField(value = "proj_code")
    private String projCode;
    /**
     * 剩余可用额度
     */
    @TableField(value = "remain_available_quota")
    private Long remainAvailableQuota;
    /**
     * 业务类型
     */
    @TableField(value = "biz_type")
    private String bizType;
    /**
     * 租赁类型
     */
    @TableField(value = "lease_type")
    private String leaseType;
    /**
     * 风控行业分类
     */
    @TableField(value = "risk_control_industry_classify")
    private String riskControlIndustryClassify;
    /**
     * 项目分类
     **/
    @TableField("proj_item")
    private String projItem;
    /**
     * 项目来源：存量翻单、渠道介绍、自主开发
     */
    @TableField("proj_source")
    private String projSource;

    /**
     * 资金用途
     */
    @TableField("funds_purpose")
    private String fundsPurpose;

    /**
     * 项目背景
     */
    @TableField("proj_background")
    private String projBackground;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;


    @Override
    public void setMainId(Long id) {
        this.id = projectDistributionId;
    }

    @Override
    public Long getMainId() {
        return this.projectDistributionId;
    }
}

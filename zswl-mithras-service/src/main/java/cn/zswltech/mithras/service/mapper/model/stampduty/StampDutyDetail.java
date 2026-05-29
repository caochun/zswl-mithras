package cn.zswltech.mithras.service.mapper.model.stampduty;

import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.common.model.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author luyujie
 * @date 2026/1/22
 * @description 印花税缴纳明细表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("stamp_duty_detail")
public class StampDutyDetail extends BaseModel implements IEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联id（合同id、融资id）
     */
    @TableField("belong_id")
    private Long belongId;

    /**
     * 申报税目名称
     */
    @TableField("name")
    private String name;

    /**
     * 业务部门id
     */
    @TableField("belong_org_id")
    private Long belongOrgId;

    /**
     * 业务部门名称
     */
    @TableField("belong_org_name")
    private String belongOrgName;

    /**
     * 客户id、机构id
     */
    @TableField("client_id")
    private Long clientId;

    /**
     * 客户名称/融资机构
     */
    @TableField("client_name")
    private String clientName;

    /**
     * 合同编号/融资编号
     */
    @TableField("belong_code")
    private String belongCode;

    /**
     * 借据id
     */
    @TableField("receipt_id")
    private Long receiptId;

    /**
     * 借据编号
     */
    @TableField("receipt_code")
    private String receiptCode;

    /**
     * 实际起租日
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 不含税租金
     */
    @TableField("rent")
    private Long rent;

    /**
     * 不含税手续费
     */
    @TableField("commission")
    private Long commission;

    /**
     * 不含税咨询费
     */
    @TableField("consulting_fee")
    private Long consultingFee;

    /**
     * 金额
     */
    @TableField("amount")
    private Long amount;

    /**
     * 印花税率
     */
    @TableField("tax_rate")
    private String taxRate;

    /**
     * 印花税
     */
    @TableField("stamp_duty")
    private String stampDuty;

    /**
     * 来源类型
     */
    @TableField("scoure")
    private String scoure;

    /**
     * 是否删除(1是、0否)
     */
    @TableField("is_delete")
    private String isDelete;

    @TableField(value = "create_by")
    private Long createBy;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField("update_by")
    private Long updateBy;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;


    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return id;
    }
}

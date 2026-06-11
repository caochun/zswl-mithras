package cn.zswltech.mithras.afterlease.mapper.model;

import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.model.SponsorField;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.state.IStateMachineEntity;
import cn.zswltech.mithras.foundation.state.ProcessStatus;
import cn.zswltech.mithras.foundation.state.ProjProcessState;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 租后调整信息表
 * @author vico
 * @date 2022-11-08
 */
@Data
@SponsorField(value = "projSponsorUserId", belongDeptField = "bizDeptId")
public class AfterLeaseAdjustInfo extends BaseModel implements Serializable, IStateMachineEntity, IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 客户id
    */
    @TableField("client_id")
    private Long clientId;

    /**
     * 项目id
     */
    @TableField("proj_id")
    private Long projId;

    /**
    * 业务类型。租赁、保理、转租赁
    */
    @TableField("biz_type")
    private String bizType;

    /**
    * 项目名称
    */
    @TableField("proj_name")
    private String projName;

    /**
    * 项目编号
    */
    @TableField("proj_code")
    private String projCode;

    /**
    * 租赁类型。直租、回租、经营性租赁
    */
    @TableField("lease_types")
    private String leaseTypes;

    /**
    * 保理类型。有追明保理、无追明保理、有追暗保理
    */
    @TableField("factoring_types")
    private String factoringTypes;

    /**
    * 转让类型。有追、无追
    */
    @TableField("zr_types")
    private String zrTypes;

    /**
    * 项目主办用户id
    */
    @TableField("proj_sponsor_user_id")
    private Long projSponsorUserId;

    /**
    * 项目协办方用户id列表
    */
    @TableField("proj_cosponsor_user_ids")
    private String projCosponsorUserIds;

    /**
    * 业务部门id
    */
    @TableField("biz_dept_id")
    private Long bizDeptId;

    /**
    * 业务部门负责人id
    */
    @TableField("biz_dept_leader_id")
    private Long bizDeptLeaderId;

    /**
    * 业务分管领导id
    */
    @TableField("biz_division_leader_id")
    private Long bizDivisionLeaderId;

    /**
    * 申报授信金额
    */
    @TableField("apply_credit_amount")
    private Long applyCreditAmount;

    /**
    * 业务调整类型，展期，调整还款类型
    */
    @TableField("after_lease_adjust_type")
    private String afterLeaseAdjustType;

    /**
    * 展期月数
    */
    @TableField(value = "extensionmonth", updateStrategy = FieldStrategy.IGNORED)
    private Long extensionmonth;

    /**
     * 调整说明
     */
    @TableField(value = "adjust_explain", updateStrategy = FieldStrategy.IGNORED)
    private String adjustExplain;

    /**
    * 调整流程状态 ProjProcessState
    */
    @TableField("adjust_process_status")
    private String adjustProcessStatus;

    //项目调整状态 预留，防止后期有关闭需求出现
    @TableField("adjust_status")
    private String adjustStatus;

    /**
    * 风控经理id
    */
    @TableField("risk_control_manager_id")
    private Long riskControlManagerId;

    /**
    * 法务经理id
    */
    @TableField("legal_manager_user_id")
    private Long legalManagerUserId;


    @Override
    public void setMainId(Long id) {
        setId(id);
    }

    @Override
    public Long getMainId() {
        return getId();
    }

    @Override
    public ProcessStatus getProcessStatus() {
        return ProjProcessState.of(adjustProcessStatus);
    }

    @Override
    public void setProcessStatus(ProcessStatus processState) {
        setAdjustProcessStatus(processState.name());
    }

    @Override
    public RecordStatus getRecordStatus() {
        return RecordStatus.of(adjustStatus);
    }

    @Override
    public void setRecordStatus(RecordStatus recordStatus) {
        setAdjustStatus(recordStatus.name());
    }

    @Override
    public String getProcessStatusFieldName() {
        return "adjust_process_status";
    }

    @Override
    public String getRecordStatusFieldName() {
        return "adjust_status";
    }
}

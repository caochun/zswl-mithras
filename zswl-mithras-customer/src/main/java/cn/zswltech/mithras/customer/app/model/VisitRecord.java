package cn.zswltech.mithras.customer.app.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import cn.zswltech.mithras.foundation.persistence.model.SponsorField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author zhouning
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "visit_record")
@SponsorField(value = "userId", belongDeptField = "deptId")
public class VisitRecord extends BaseModelWithLogicDelete {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    /**
     * 客户id
     */
    @TableField("client_id")
    private Long clientId;

    /**
     * 客户名称
     */
    @TableField("client_name")
    private String clientName;

    /**
     * 拜访方式
     */
    @TableField("visit_way")
    private String visitWay;

    /**
     * 拜访类型
     */
    @TableField("visit_type")
    private String visitType;

    /**
     * 拜访阶段
     */
    @TableField("visit_phase")
    private String visitPhase;

    /**
     * 项目编号
     */
    @TableField("proj_code")
    private String projCode;

    /**
     * 合同id
     */
    @TableField("contract_id")
    private Long contractId;

    /**
     * 合同编号
     */
    @TableField("contract_code")
    private String contractCode;

    /**
     * 租后检查计划id
     */
    @TableField("check_plan_id")
    private Long checkPlanId;

    /**
     * 打卡日期/补卡日期
     */
    @TableField("check_in_date")
    private LocalDateTime checkInDate;


    /**
     * 打卡地点/补卡地点
     */
    @TableField("check_in_location")
    private String checkInLocation;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 部门id
     */
    @TableField("dept_id")
    private Long deptId;

    /**
     * 记录状态
     */
    @TableField("status")
    private String status;
}

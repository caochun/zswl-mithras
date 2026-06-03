package cn.zswltech.mithras.dashboard.application.guanyuandata.boss;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.dashboard.application.guanyuandata.GuanYuanColumnPopulate;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2024/5/21
 * @description
 */
@Data
public class OperationYYContractApprovalDTO implements GuanYuanColumnPopulate {
    private Long contractId;
    private Long processId;
    private String projName;
    private String projCode;
    private String contractCode;
    private Long clientId;
    private String clientName;
    private Long projSponsorUserId;
    private String projSponsorUserName;
    private Long bizDeptId;
    private String bizDeptName;
    /**
     * 业务分类： 产业类，公用类 BusinessGroupEnum
     */
    private String businessGroup;

    /**
     * 业务模式： 租赁类型-直租，回租，经营性租赁，保理类型-。。。
     */
    private String businessModel;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

    /**
     * 流程结束时间
     */
    private LocalDateTime endTime;

    /**
     * 发起人(单位/小时)
     */
    private BigDecimal startUser;

    /**
     * 发起人确认(单位/小时)
     */
    private BigDecimal startUserConfirm;

    /**
     * 业务部负责人(单位/小时)
     */
    private BigDecimal bizDeptMaster;

    /**
     * 业务部分管领导(单位/小时)
     */
    private BigDecimal bizDivisionLeader;

    /**
     * 财务主管(单位/小时)
     */
    private BigDecimal financialOfficer;

    /**
     * 财务总监(单位/小时)
     */
    private BigDecimal chiefFinancialOfficer;

    /**
     * 运营经办(单位/小时)
     */
    private BigDecimal yunYingGuanLiJb;

    /**
     * 法务经理(单位/小时)
     */
    private BigDecimal lawManager;

    /**
     * 法务经理确认(单位/小时)
     */
    private BigDecimal lawManagerConfirm;

    /**
     * 运营复核(单位/小时)
     */
    private BigDecimal yunYingGuanLiFh;

    /**
     * 运营部负责人(单位/小时)
     */
    private BigDecimal yunYingPrincipal;

    /**
     * 流程总耗时(单位/工作日)
     */
    private BigDecimal processTime;

    /**
     * 运营经办到达时间
     */
    private LocalDateTime yunYingJbStartTime;

    /**
     * 运营经办提交时间
     */
    private LocalDateTime yunYingJbEndTime;

    /**
     * 运营复核到达时间
     */
    private LocalDateTime yunYingFhStartTime;

    /**
     * 运营复核提交时间
     */
    private LocalDateTime yunYingFhEndTime;

    /**
     * 运营部总耗时(单位/小时)
     */
    private BigDecimal yunYingTime;

    @Override
    public void populate(Map<String, String> map) {

        this.contractId = Optional.ofNullable(map.get("合同id")).map(Long::valueOf).orElse(null);
        this.processId = Optional.ofNullable(map.get("流程id")).map(Long::valueOf).orElse(null);
        this.projName = map.get("项目名称");
        this.projCode = map.get("项目编号");
        this.contractCode = map.get("合同编号");
        this.clientId = Optional.ofNullable(map.get("client_id")).map(Long::valueOf).orElse(null);
        this.clientName = map.get("承租人/债权人");
        this.projSponsorUserId = Optional.ofNullable(map.get("proj_sponsor_user_id")).map(Long::valueOf).orElse(null);
        this.projSponsorUserName = map.get("主办业务员");
        this.bizDeptId = Optional.ofNullable(map.get("biz_dept_id")).map(Long::valueOf).orElse(null);
        this.bizDeptName = map.get("部门");
        this.businessGroup = map.get("项目分类");
        this.businessModel = map.get("项目类型");
        this.applyTime = Optional.ofNullable(map.get("合同审批发起时间")).map(d -> LocalDateTimeUtil.parse(d, "yyyy-MM-dd HH:mm:ss")).orElse(null);
        this.endTime = Optional.ofNullable(map.get("合同审批通过时间")).map(d -> LocalDateTimeUtil.parse(d, "yyyy-MM-dd HH:mm:ss")).orElse(null);
        this.processTime = Optional.ofNullable(map.get("合同耗时(工作日分钟)")).map(m -> new BigDecimal(m).divide(new BigDecimal("1440"), 2, RoundingMode.HALF_UP)).orElse(null);

        this.startUser = Optional.ofNullable(map.get("发起人")).map(BigDecimal::new).orElse(null);
        this.startUserConfirm = Optional.ofNullable(map.get("发起人确认")).map(BigDecimal::new).orElse(null);
        this.bizDeptMaster = Optional.ofNullable(map.get("业务部负责人")).map(BigDecimal::new).orElse(null);
        this.bizDivisionLeader = Optional.ofNullable(map.get("业务分管领导")).map(BigDecimal::new).orElse(null);
        this.financialOfficer = Optional.ofNullable(map.get("财务主管")).map(BigDecimal::new).orElse(null);
        this.chiefFinancialOfficer = Optional.ofNullable(map.get("财务总监")).map(BigDecimal::new).orElse(null);
        this.yunYingGuanLiJb = Optional.ofNullable(map.get("运营管理（经办）")).map(BigDecimal::new).orElse(null);
        this.lawManager = Optional.ofNullable(map.get("法务经理")).map(BigDecimal::new).orElse(null);
        this.lawManagerConfirm = Optional.ofNullable(map.get("法务经理确认")).map(BigDecimal::new).orElse(null);
        this.yunYingGuanLiFh = Optional.ofNullable(map.get("运营管理（复核）")).map(BigDecimal::new).orElse(null);
        this.yunYingPrincipal = Optional.ofNullable(map.get("运营部负责人1")).map(BigDecimal::new).orElse(Optional.ofNullable(map.get("运营部负责人2")).map(BigDecimal::new).orElse(null));

    }
}

















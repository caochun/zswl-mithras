package cn.zswltech.mithras.service.service.dashboard.guanyuandata;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2025/1/5
 * @description
 */
@Data
public class ZLHeTongShiXiaoDTO implements GuanYuanColumnPopulate {
    /**
     * 流程ID
     */
    private String processInstanceId;
    /**
     * 合同ID
     */
    private Long contractId;
    /**
     * 合同编号
     */
    private String contractCode;
    /**
     * 项目主办ID
     */
    private Long sponsorUserId;
    /**
     * 项目主办名称
     */
    private String sponsorUserName;
    /**
     * 流程开始时间
     */
    private LocalDateTime processStartTime;
    /**
     * 流程结束时间
     */
    private LocalDateTime processEndTime;
    /**
     * 部门ID
     */
    private Long bizDeptId;
    /**
     * 部门名称
     */
    private String bizDeptName;
    /**
     * 业务类型
     */
    private String businessCategory;
    /**
     * 租赁类型
     */
    private String leaseType;
    /**
     * 全流程耗时（工作日小时）
     */
    private String duration;
    /**
     * 运营经办耗时（工作日小时）
     */
    private String durationYYJB;
    /**
     * 运营复核耗时（工作日小时）
     */
    private String durationYYFH;
    /**
     * 运营负责人耗时（工作日小时）
     */
    private String durationYYFZR;
    /**
     * 法务经理耗时（工作日小时）
     */
    private String durationFWJL;
    /**
     * 法务负责人耗时（工作日小时）
     */
    private String durationFWFZR;
    /**
     * 财务主管耗时（工作日小时）
     */
    private String durationCWZG;

    @Override
    public void populate(Map<String, String> map) {
        this.setProcessInstanceId(Optional.ofNullable(map.get("流程id")).orElse(""));
        this.setContractId(Optional.ofNullable(map.get("合同id")).map(Long::valueOf).orElse(null));
        this.setContractCode(Optional.ofNullable(map.get("合同编号")).orElse(""));
        this.setSponsorUserId(Optional.ofNullable(map.get("proj_sponsor_user_id")).map(Long::valueOf).orElse(null));
        this.setSponsorUserName(Optional.ofNullable(map.get("主办业务员")).orElse(""));
        this.setProcessStartTime(Optional.ofNullable(map.get("合同审批发起时间")).map(e -> LocalDateTimeUtil.parse(e, DatePattern.NORM_DATETIME_PATTERN)).orElse(null));
        this.setProcessEndTime(Optional.ofNullable(map.get("合同审批通过时间")).map(e -> LocalDateTimeUtil.parse(e, DatePattern.NORM_DATETIME_PATTERN)).orElse(null));
        this.setBizDeptId(Optional.ofNullable(map.get("biz_dept_id")).map(Long::valueOf).orElse(null));
        this.setBizDeptName(Optional.ofNullable(map.get("部门")).orElse(""));
        this.setBusinessCategory(Optional.ofNullable(map.get("项目分类")).orElse(""));
        this.setLeaseType(Optional.ofNullable(map.get("项目类型")).orElse(""));
        String s = Optional.ofNullable(map.get("合同耗时(工作日分钟)")).orElse("0");
        this.setDuration(new BigDecimal(s).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP).toPlainString());
        this.setDurationYYJB(Optional.ofNullable(map.get("运营管理（经办）")).orElse("0"));
        this.setDurationYYFH(Optional.ofNullable(map.get("运营管理（复核）")).orElse("0"));
        this.setDurationYYFZR(Optional.ofNullable(map.get("运营部负责人")).orElse("0"));
        this.setDurationFWJL(Optional.ofNullable(map.get("法务经理")).orElse("0"));
        this.setDurationFWFZR(Optional.ofNullable(map.get("法律合规部负责人")).orElse("0"));
        this.setDurationCWZG(Optional.ofNullable(map.get("财务主管")).orElse("0"));
    }
}

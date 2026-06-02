package cn.zswltech.mithras.metric.emit.model.req.risk.event;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yibin
 */
@Data
public class RiskEventReqSingleEvent {

    private String eventName;//	string	必须	事件名称	最多255字
    private LocalDateTime admitDate;//	string	必须	事件认定日期	“YYYY-MM-dd hh:mm:ss”
    private LocalDateTime occurDate;//	string	必须	事件发生日期	“YYYY-MM-dd hh:mm:ss”
    private List<String> speReportFileKeys;//	string []	非必须	事件专项报告文件标识列表
    private String occurPlace;//	string	必须	事件发生地点	最多255字，发生主体所在地
    private String summary;//	string	必须	事件概述	最多500字
    private String occurOrg;//	string	必须	发生机构
    private String occurDept;//	string	必须	发生部门
    private BigDecimal totalAmount;//	number	必须	事件总金额（万元）	小数点前最大15位，保留两位小数
    private BigDecimal recoveredAmount;//	number	必须	已收回金额（万元）	小数点前最大15位，保留两位小数
    private String bizType;//	string		业务类型;
    private BigDecimal ownFunds;//	number	必须	涉及自有资金投资金额（万元）	小数点前最大15位，保留两位小数
    private String majorRiskType;//	string	必须	事件涉及主要风险；
    private List<String> otherRiskType;// string[] 非必须 事件涉及其他风险；
    private BigDecimal exposureAmount;// number  必须 预计风险敞口金额（万元）小数点前最大15位，保留两位小数
    private BigDecimal lossAmount;// number 必须 实际损失金额（万元）小数点前最大15位，保留两位小数
    private BigDecimal totalLossAmount;// number 必须 累计计提减值损失（万元）小数点前最大15位，保留两位小数
    private String regulatoryLevel;// string 必须 监管影响等级；
    private String reputationLevel;// string 必须 声誉影响等级；
    private String operationLevel;// string 必须 运营影响等级；
    private String lawLevel;// string 必须 法律影响等级；
    private String unFinDesc;// string 必须 非财务影响说明
    private List<String> submitScope;// string [] 必须 报送范围列表；
    private String innerReview;// string 非必须 本单位内部报送情况- 事件汇报及审议情况 最多500字，若选择多条报送范围数据，则需要有对应的汇报及审议情况内容；如：党委：XXXXXXXXXXXX 董事会：XXXXXXXXX
    private LocalDateTime groupReportDate;//string 非必须 事件报告集团时间	“YYYY-MM-dd hh:mm:ss”
    private String groupAcceptDept;// string 非必须 集团接收部门
    private List<String> groupRepFileName;// string [] 非必须 集团报送文件名称 最多255字
    private String regulatoryEventReview;// string 非必须 监管报送情况-事件汇报及审议情况 最多500字
    private String dealOrg;// string 必须 处置责任机构
    private String dealAccount;// string 必须 处置负责人
    private String dealPlan;// string 必须 处置方案 最多500字
    private BigDecimal totalRecoveredAmount;// number 必须 累计收回金额（万元）小数点前最大15位，保留两位小数
    private BigDecimal yearRecoveredAmount;// number 必须 年内收回目标（万元）小数点前最大15位，保留两位小数
    private String dealResult;// string 必须 风险事件发生后至今取得处置成果 最多500字
    private String dealDifficulty;// string 非必须 存在的困难 最多500字，有则报送
    private String nextDealPlan;// string非必须 下一步处置计划 最多500字
    private LocalDateTime dealEndDate;// string 必须 预计处置完成时间	“YYYY-MM-dd hh:mm:ss”
    private String dealStatus;// string 必须 处置状态；
    private List<String> dealFileKeys;// string [] 非必须 处置相关附件标识列表
    private Boolean assess; //boolean 必须 是否纳入绩效考核
    private String assessResult;//string 必须 考核方式及结果 最多500字
    private String unAssessDesc;// string 非必须 未纳入考核原因 最多500字，不纳入绩效考核时必须
    private Boolean accountable;// boolean 必须 是否涉及追责
    private String dutyDesc;// string 必须 追责举措方案 最多500字
    private String unDutyDesc;// string 必须 未追责原因 最多500字
    private String dutyResult;// string 必须 追责结果 最多500字
    private String nextDutyPlan;// string 必须 下一步追责计划 最多500字
    private String eventSource;// string 必须 事件来源；
    private String eventLevel;// string 必须 事件分级；
    private List<String> riskContent;// string [] 必须 涉及风险内容列表；

}
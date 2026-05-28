package cn.zswltech.mithras.service.service.dashboard.guanyuandata;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2025/10/21
 * @description 观远BI-投放收益率表数据集（字段不全，只取了一些需要使用的，后续要使用再加）
 */
@Data
public class PayIncomeDTO implements GuanYuanColumnPopulate {
    private Long id;
    private Long deptId;
    private Long sponsorUserId;
    private LocalDate payDate;
    private Long clientId;
    private String receiptCode;
    private String contractCode;
    private String areaDisplay;
    private String industryDisplay;
    private String riskControlIndustryClassifyDisplay;
    private String leaseTypeDisplay;
    private Long projectAmount;
    private String orgScaleDisplay;

    @Override
    public void populate(Map<String, String> map) {
        this.id = Optional.ofNullable(map.get("id")).map(Long::valueOf).orElse(null);
        this.deptId = Optional.ofNullable(map.get("业务组id")).map(Long::valueOf).orElse(null);
        this.sponsorUserId = Optional.ofNullable(map.get("主办人员id")).map(Long::valueOf).orElse(null);
        this.payDate = Optional.ofNullable(map.get("放款时间")).map(e -> LocalDateTimeUtil.parse(e, DatePattern.NORM_DATETIME_PATTERN).toLocalDate()).orElse(null);
        this.clientId = Optional.ofNullable(map.get("客户id")).map(Long::valueOf).orElse(null);
        this.receiptCode = Optional.ofNullable(map.get("借据编号")).orElse("");
        this.contractCode = Optional.ofNullable(map.get("合同编号")).orElse("");
        this.areaDisplay = Optional.ofNullable(map.get("地区")).orElse("");
        this.industryDisplay = Optional.ofNullable(map.get("行业")).orElse("");
        this.riskControlIndustryClassifyDisplay = Optional.ofNullable(map.get("风险策略")).orElse("");
        this.leaseTypeDisplay = Optional.ofNullable(map.get("项目类型")).orElse("");
        this.projectAmount = Optional.ofNullable(map.get("项目金额")).map(Long::valueOf).orElse(null);
        this.orgScaleDisplay = Optional.ofNullable(map.get("公司类型")).orElse("");
    }
}

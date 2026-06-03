package cn.zswltech.mithras.dashboard.application.guanyuandata.boss;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.dashboard.application.guanyuandata.GuanYuanColumnPopulate;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2024/5/21
 * @description
 */
@Data
public class OperationEfficiencyStageDTO implements GuanYuanColumnPopulate {
    private Long itemId;
    private String itemName;
    private Long moneyAmount;
    private LocalDateTime stageStartTime;
    private LocalDateTime stageEndTime;
    private Integer workdays;
    private String riskControlIndustryClassify;
    private Long projSponsorUserId;
    private Long bizDeptId;

    @Override
    public void populate(Map<String, String> map) {
        this.setItemId(Optional.ofNullable(map.get("item_id")).map(Long::valueOf).orElse(null));
        this.setItemName(Optional.ofNullable(map.get("item_name")).orElse(null));
        this.setMoneyAmount(Optional.ofNullable(map.get("money_amount")).map(Long::valueOf).orElse(null));
        this.setStageStartTime(Optional.ofNullable(map.get("stage_start_time")).map(e -> LocalDateTimeUtil.parse(e, DatePattern.NORM_DATETIME_PATTERN)).orElse(null));
        this.setStageEndTime(Optional.ofNullable(map.get("stage_end_time")).map(e -> LocalDateTimeUtil.parse(e, DatePattern.NORM_DATETIME_PATTERN)).orElse(null));
        this.setWorkdays(Optional.ofNullable(map.get("workdays")).map(Integer::valueOf).orElse(null));
        this.setRiskControlIndustryClassify(Optional.ofNullable(map.get("risk_control_industry_classify")).orElse(null));
        this.setProjSponsorUserId(Optional.ofNullable(map.get("proj_sponsor_user_id")).map(Long::valueOf).orElse(null));
        this.setBizDeptId(Optional.ofNullable(map.get("biz_dept_id")).map(Long::valueOf).orElse(null));
    }
}

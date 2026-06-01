package cn.zswltech.mithras.service.enums.creditreport;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

/**
 * 企业借贷交易业务类型代码表
 * @date 2023/2/20
 * @description
 */
@AllArgsConstructor
@Getter
public enum CreditReportBusinessTypeEnum implements PullDown {

    OUT_STANDING_LOAD_TRANSACTIONS("未结清借贷交易汇总信息段"),
    SECURED_TRANSACTION_SUMMARY_UNIT("未结清担保交易汇总信息段"),
    SECURED_TRANSACTION_SUMMARY_UNIT_OTHER("未结清其他担保交易汇总信息段"),
    ;

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    //需要添加合计
    public static List<String> needCount() {
        return ListUtil.toList(SECURED_TRANSACTION_SUMMARY_UNIT.name(), SECURED_TRANSACTION_SUMMARY_UNIT_OTHER.name());
    }

    public static CreditReportBusinessTypeEnum finaByName(String name) {
        for (CreditReportBusinessTypeEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }
}

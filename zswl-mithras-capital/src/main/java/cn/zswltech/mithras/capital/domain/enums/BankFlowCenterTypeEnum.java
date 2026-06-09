package cn.zswltech.mithras.capital.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/5/18/11:20
 * @description 银行流水tab名称枚举
 */
@Getter
@AllArgsConstructor
public enum BankFlowCenterTypeEnum implements PullDown {
    PROCESSING_CENTER("处理中心"),
    PROCESSED_PROJ_SIDE("已处理-项目端"),
    PROCESSED_FUNDS_END("已处理-资金端"),
    NO_PROCESSING_REQUIRE("无需处理"),
    ;

    private final String display;

    @Override
    public String display() {
        return display;
    }
}

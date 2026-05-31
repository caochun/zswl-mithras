package cn.zswltech.mithras.service.enums.capital;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/5/18/14:13
 * @description 核销类型
 */
@Getter
@AllArgsConstructor
public enum BankFlowWriteOffTypeEnum implements PullDown {
    PROJ_SIDE("项目端"),
    FUNDS_END("资金端"),
    NO_PROCESSING_REQUIRE("无需处理"),
    ;

    private final String display;

    @Override
    public String display() {
        return display;
    }
}

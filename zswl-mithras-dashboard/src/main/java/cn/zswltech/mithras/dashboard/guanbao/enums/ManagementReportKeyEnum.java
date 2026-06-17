package cn.zswltech.mithras.dashboard.guanbao.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2024/12/21
 * @description
 */
@AllArgsConstructor
@Getter
public enum ManagementReportKeyEnum {
    YE_WU_YUN_XING_FEN_XI("业务运行分析表"),
    YUN_YING_DAI_BAN("运营待办管理表"),
    HE_TONG_SHI_XIAO("合同时效监控表");

    private String display;
}

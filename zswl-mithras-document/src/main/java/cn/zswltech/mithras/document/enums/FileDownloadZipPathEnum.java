package cn.zswltech.mithras.document.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 文件下载压缩路径（一级）
 *
 * @author wangchuanhao
 * @date 2023/2/10 2:22 PM
 */
@AllArgsConstructor
@Getter
public enum FileDownloadZipPathEnum {

    CLIENT("客户资料"),
    PROJ_ESTABLISH("立项资料"),
    PROJ_REVIEW("项目评审"),
    PAYMENT("付款申请管理"),
    PAID_RECORD("付款记录管理"),
    MARGIN_RECORD("保证金管理"),
    COLLECTION("收款核销"),

    GROUP_CREDIT_ESTABLISH("集团授信立项资料"),
    GROUP_CREDIT_REVIEW("集团授信评审资料"),

    FUND_GUARANTEE_INFO("财务资金管理担保信息材料"),
    FUND_CREDIT("财务资金管理授信管理材料"),

    FTP_QUARTERLY_GUIDANCE("季度最低收益率指导资料"),
    RISK_METRIC_FACTOR_FILE("风险指标财务报表"),
    FTP_MONTHLY_GUIDANCE("月度ftp定价指导资料"),
    ARCHIVES("档案管理"),
    ;

    private static Map<String, FileDownloadZipPathEnum> map;

    static {
        map = Stream.of(FileDownloadZipPathEnum.values()).collect(Collectors.toMap(FileDownloadZipPathEnum::name, e -> e));
    }

    public final String display;

    public static FileDownloadZipPathEnum of(String name) {
        return map.get(name);
    }

}

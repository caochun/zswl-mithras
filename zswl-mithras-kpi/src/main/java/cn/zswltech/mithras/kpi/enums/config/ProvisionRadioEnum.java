package cn.zswltech.mithras.kpi.enums.config;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/2/13
 * @description 费用计提比例参数值枚举
 */
@Getter
@AllArgsConstructor
public enum ProvisionRadioEnum implements PullDown {
    PUBLIC_NORMAL("PUBLIC", "NORMAL", "公用事业类-正常类"),
    PUBLIC_ATTENTION("PUBLIC", "ATTENTION", "公用事业类-关注类"),
    PUBLIC_SECONDARY("PUBLIC", "SECONDARY", "公用事业类-次级类"),
    PUBLIC_SUSPICIOUS("PUBLIC", "SUSPICIOUS", "公用事业类-可疑类"),
    PUBLIC_LOSS("PUBLIC", "LOSS", "公用事业类-损失类"),

    CONSTRUCTION_MACHINERY_NORMAL("CONSTRUCTION_MACHINERY", "NORMAL", "工程机械类-正常类"),
    CONSTRUCTION_MACHINERY_ATTENTION("CONSTRUCTION_MACHINERY", "ATTENTION", "工程机械类-关注类"),
    CONSTRUCTION_MACHINERY_SECONDARY("CONSTRUCTION_MACHINERY", "SECONDARY", "工程机械类-次级类"),
    CONSTRUCTION_MACHINERY_SUSPICIOUS("CONSTRUCTION_MACHINERY", "SUSPICIOUS", "工程机械类-可疑类"),
    CONSTRUCTION_MACHINERY_LOSS("CONSTRUCTION_MACHINERY", "LOSS", "工程机械类-损失类"),

    OTHER_NORMAL("OTHER", "NORMAL", "其他-正常类"),
    OTHER_ATTENTION("OTHER", "ATTENTION", "其他-关注类"),
    OTHER_SECONDARY("OTHER", "SECONDARY", "其他-次级类"),
    OTHER_SUSPICIOUS("OTHER", "SUSPICIOUS", "其他-可疑类"),
    OTHER_LOSS("OTHER", "LOSS", "其他-损失类");

    private final String projectClassify;
    private final String assetClassify;
    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}

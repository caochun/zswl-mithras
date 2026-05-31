package cn.zswltech.mithras.service.enums.client;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/10/21
 * @description 自然人客户资料大类
 */
@AllArgsConstructor
@Getter
public enum NormalClientMaterialTypeEnum implements PullDown, IMaterialsTypeConvert {
    BASIC_INFORMATION("基础资料", "normalClientBasicSubTypeEnum"),
    OTHERS("其他", "normalClientOthersSubTypeEnum");

    private final String display;
    private final String subEnumName;

    @Override
    public String businessModule() {
        return "CLIENT";
    }

    @Override
    public String display() {
        return this.display;
    }

    @Override
    public String childSelectName() {
        return this.subEnumName;
    }

    public static List<String> needCopyType() {
        return ListUtil.of(
                BASIC_INFORMATION.name(),
                OTHERS.name()
        );
    }
}

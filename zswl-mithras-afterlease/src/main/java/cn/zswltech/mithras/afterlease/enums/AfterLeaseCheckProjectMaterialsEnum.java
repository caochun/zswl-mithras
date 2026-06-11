package cn.zswltech.mithras.afterlease.enums;

import cn.zswltech.mithras.foundation.metadata.IMaterialsTypeConvert;
import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @deprecated 使用新类 NewAfterLeaseCheckMaterialsEnum 替代
 */
@AllArgsConstructor
@Getter
@Deprecated
public enum AfterLeaseCheckProjectMaterialsEnum implements PullDown, IMaterialsTypeConvert {

    // 检查计划总结报告
    CHECK_PLAN_SUMMARY_REPORT("租后检查总结报告", new ArrayList<>(), 0),
    // 检查报告附件
    CHECK_PROJECT_NON_PUBLIC_REPORT_ATTACHMENT("检查报告附件", new ArrayList<>(), 0),
    // 公用事业模板财务数据
    CHECK_PROJECT_PUBLIC_REPORT_FINANCE("公用事业模板财务数据", new ArrayList<>(), 0)
    ;

    private final String display;
    /**
     * 审批流中可处理节点列表
     */
    private final List<String> canHandleActivityIdList;

    /**
     * 排序优先级
     */
    private final int sort;

    private static final Map<String, AfterLeaseCheckProjectMaterialsEnum> map;

    static {
        map = Stream.of(AfterLeaseCheckProjectMaterialsEnum.values()).collect(Collectors.toMap(AfterLeaseCheckProjectMaterialsEnum::name, e -> e));
    }

    public static AfterLeaseCheckProjectMaterialsEnum getByName(String name) {
        return map.get(name);
    }

    public static List<String> listAll() {
        return new ArrayList<>(map.keySet());
    }


    @Override
    public String businessModule() {
        return "AFTER_LEASE_CHECK_PROJECT";
    }

    @Override
    public String display() {
        return display;
    }
}

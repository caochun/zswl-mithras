package cn.zswltech.mithras.afterlease.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dingqi
 * @date 2023/6/2
 * @description
 */
@AllArgsConstructor
@Getter
public enum NewAfterLeaseCheckMaterialsEnum implements PullDown, IMaterialsTypeConvert {

    // 检查计划总结报告
    CHECK_PLAN_SUMMARY_REPORT("租后检查总结报告", new ArrayList<>(), 0),
    // 非公用事业模板检查报告附件
    CHECK_REPORT_NON_PUBLIC_ATTACHMENT("非公用事业模板检查报告附件", new ArrayList<>(), 0),
    // 公用事业模板财务数据
    CHECK_REPORT_PUBLIC_FINANCE("公用事业模板财务数据", new ArrayList<>(), 0)
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

    private static final Map<String, NewAfterLeaseCheckMaterialsEnum> map;

    static {
        map = Stream.of(NewAfterLeaseCheckMaterialsEnum.values()).collect(Collectors.toMap(NewAfterLeaseCheckMaterialsEnum::name, e -> e));
    }

    public static NewAfterLeaseCheckMaterialsEnum getByName(String name) {
        return map.get(name);
    }

    public static List<String> listAll() {
        return new ArrayList<>(map.keySet());
    }


    @Override
    public String businessModule() {
        return "NEW_AFTER_LEASE_CHECK_REPORT";
    }

    @Override
    public String display() {
        return display;
    }
}

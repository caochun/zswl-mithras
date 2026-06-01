package cn.zswltech.mithras.service.enums.afterlease;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.constant.FlowConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum AfterLeaseAdjustMaterialsEnum implements PullDown, IMaterialsTypeConvert {

    // 尽调报告（改名为项目报告 since 2022.09.13）
    // 又改回为尽调报告 since 2022.09.15）
    DUE_DILIGENCE_REPORT("业务申请资料", new ArrayList<>(), 0),
    // 审查报告
    RISK_REVIEW_REPORT("审查报告", Arrays.asList(FlowConstants.PARALLEL_RISK_MANAGER, FlowConstants.PARALLEL_RISK_MANAGER_BACK), 1),
    // 法律合规意见书
    LEGAL_COMPLIANCE_REPORT("法律合规意见书", Arrays.asList(FlowConstants.PARALLEL_LAW_MANAGER, FlowConstants.PARALLEL_LAW_MANAGER_BACK), 2),
    // 总经办会议纪要
    //GMO_MEETING_MINUTES("总经办会议纪要", Collections.singletonList("userTask_officeSecretary"), 5),
    // 评审会会议纪要
    MEETING_REVIEW_REPORT("评审会会议纪要", Arrays.asList("userTask_jurySecretaryCollect", "userTask_juryMeetingCollect"), 3),
    // 评审会会议记录
    MEETING_REVIEW_RECORD("评审会会议记录", Arrays.asList("userTask_jurySecretaryCollect", "userTask_juryMeetingCollect"), 4),
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

    private static final Map<String, AfterLeaseAdjustMaterialsEnum> map;

    static {
        map = Stream.of(AfterLeaseAdjustMaterialsEnum.values()).collect(Collectors.toMap(AfterLeaseAdjustMaterialsEnum::name, e -> e));
    }

    public static AfterLeaseAdjustMaterialsEnum getByName(String name) {
        return map.get(name);
    }

    public static List<String> listAll() {
        return new ArrayList<>(map.keySet());
    }


    public static List<String> listAdjust() {
        return Arrays.asList(DUE_DILIGENCE_REPORT.name(), RISK_REVIEW_REPORT.name(), LEGAL_COMPLIANCE_REPORT.name(),
                 MEETING_REVIEW_REPORT.name());
    }

    @Override
    public String businessModule() {
        return "ADJUST";
    }

    @Override
    public String display() {
        return display;
    }
}

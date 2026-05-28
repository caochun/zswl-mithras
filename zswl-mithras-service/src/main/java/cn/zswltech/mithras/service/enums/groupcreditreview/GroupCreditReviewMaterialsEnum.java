package cn.zswltech.mithras.service.enums.groupcreditreview;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.constant.FlowConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 集团授信评审资料清单
 *
 * @author wangchuanhao
 * @date 2022/8/9 12:48 AM
 */
@AllArgsConstructor
@Getter
public enum GroupCreditReviewMaterialsEnum implements PullDown, IMaterialsTypeConvert {

    // 尽调报告（改名为项目报告 since 2022.09.13）
    // 又改回为尽调报告 since 2022.09.15）
    DUE_DILIGENCE_REPORT("尽调报告", new ArrayList<>(), 0),
    // 业务定价审批表
//    BUSINESS_PRICING_APPROVAL_FORM("业务定价审批表", new ArrayList<>(), 1),
    // 审查报告
    RISK_REVIEW_REPORT("审查报告", Arrays.asList(FlowConstants.PARALLEL_RISK_MANAGER, FlowConstants.PARALLEL_RISK_MANAGER_BACK), 2),
    // 法律合规意见书
    LEGAL_COMPLIANCE_REPORT("法律合规意见书", Arrays.asList(FlowConstants.PARALLEL_LAW_MANAGER, FlowConstants.PARALLEL_LAW_MANAGER_BACK), 3),
    // 项目收益率审查意见书
    YIELD_REVIEW_REPORT("项目收益率审查意见书", Arrays.asList("userTask_financeOfficer", "userTask_financeOfficer_2"), 4),
    // 总经办会议纪要
    GMO_MEETING_MINUTES("总经办会议纪要", Collections.singletonList("userTask_officeSecretary"), 5),
    // 评审会会议纪要
    MEETING_REVIEW_REPORT("评审会会议纪要", Arrays.asList("userTask_jurySecretaryCollect", "userTask_juryMeetingCollect"), 6),
    // 评审会会议记录
    MEETING_REVIEW_RECORD("评审会会议记录", Arrays.asList("userTask_jurySecretaryCollect", "userTask_juryMeetingCollect"), 7),
    // 其他
    OTHER("其他", new ArrayList<>(), 8),
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

    private static final Map<String, GroupCreditReviewMaterialsEnum> map;

    static {
        map = Stream.of(GroupCreditReviewMaterialsEnum.values()).collect(Collectors.toMap(GroupCreditReviewMaterialsEnum::name, e -> e));
    }

    public static GroupCreditReviewMaterialsEnum getByName(String name) {
        return map.get(name);
    }

    public static List<String> listAll() {
        return new ArrayList<>(map.keySet());
    }

    public static List<String> listPricing() {
        return Arrays.asList(YIELD_REVIEW_REPORT.name(), GMO_MEETING_MINUTES.name());
    }

    public static List<String> listReview() {
        return Arrays.asList(DUE_DILIGENCE_REPORT.name(), RISK_REVIEW_REPORT.name(), LEGAL_COMPLIANCE_REPORT.name(),
                YIELD_REVIEW_REPORT.name(), MEETING_REVIEW_REPORT.name(), MEETING_REVIEW_RECORD.name(), OTHER.name());
    }

    @Override
    public String businessModule() {
        return "GROUP_CREDIT_REVIEW";
    }

    @Override
    public String display() {
        return display;
    }
}

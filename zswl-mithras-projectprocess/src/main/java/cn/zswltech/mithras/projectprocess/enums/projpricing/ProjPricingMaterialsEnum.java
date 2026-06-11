package cn.zswltech.mithras.projectprocess.enums.projpricing;

import cn.zswltech.mithras.foundation.metadata.IMaterialsTypeConvert;
import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 项目评审资料清单
 *
 * @author wangchuanhao
 * @date 2022/8/9 12:48 AM
 */
@AllArgsConstructor
@Getter
public enum ProjPricingMaterialsEnum implements PullDown, IMaterialsTypeConvert {

    // 尽调报告（改名为项目报告 since 2022.09.13）
    // 又改回为尽调报告 since 2022.09.15）
//    DUE_DILIGENCE_REPORT("尽调报告", new ArrayList<>(), 0),
    // 业务定价审批表
    BUSINESS_PRICING_APPROVAL_FORM("业务定价审批表", new ArrayList<>(), 10),
    // 业务定价会议纪要
    BUSINESS_PRICING_APPROVAL_MEETING_REPORT("业务定价会议纪要", Collections.singletonList("userTask_financeOfficer_2"), 10),
    // 审查报告
//    RISK_REVIEW_REPORT("审查报告", Arrays.asList(FlowConstants.PARALLEL_RISK_MANAGER, FlowConstants.PARALLEL_RISK_MANAGER_BACK), 20),
//    // 法律合规意见书
//    LEGAL_COMPLIANCE_REPORT("法律合规意见书", Arrays.asList(FlowConstants.PARALLEL_LAW_MANAGER, FlowConstants.PARALLEL_LAW_MANAGER_BACK), 30),
//    //专职评审意见
//    PROFESSIONAL_REVIEW_COMMENTS("专职评审意见", Collections.singletonList("userTask_fullReviewCommittee"), 35),
    // 项目收益率审查意见书
    YIELD_REVIEW_REPORT("项目收益率审查意见书", Arrays.asList("userTask_financeOfficer", "userTask_financeOfficer_2", "userTask_financeOfficer_1"), 40),
//    // 总经办会议纪要
//    GMO_MEETING_MINUTES("总经办会议纪要",  Arrays.asList("userTask_officeSecretary"), 50),
//    // 评审会会议纪要
//    MEETING_REVIEW_REPORT("评审会会议纪要", Arrays.asList("userTask_jurySecretaryCollect", "userTask_juryMeetingCollect"), 60),
//    // 评审会会议记录
//    MEETING_REVIEW_RECORD("评审会会议记录", Arrays.asList("userTask_jurySecretaryCollect", "userTask_juryMeetingCollect"), 70),
//    // 董事会会议纪要
//    DIRECTOR_MEETING_REPORT("董事会会议纪要",  Arrays.asList("userTask_directorSecretaryCollect"), 80),
    // 其他
//    OTHER("其他", new ArrayList<>(), 90),
    PRICING_OTHER("其他", new ArrayList<>(), 90),
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

    private static final Map<String, ProjPricingMaterialsEnum> map;

    static {
        map = Stream.of(ProjPricingMaterialsEnum.values()).collect(Collectors.toMap(ProjPricingMaterialsEnum::name, e -> e, (m1,m2) -> m1));
    }

    public static ProjPricingMaterialsEnum getByName(String name) {
        return map.get(name);
    }

    public static List<String> listAll() {
        return new ArrayList<>(map.keySet());
    }

    @Override
    public String businessModule() {
        return "PROJ_PRICING";
    }

    @Override
    public String display() {
        return display;
    }
}

package cn.zswltech.mithras.service.enums.filingmaterials;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public enum FilingDirectoryEnum {
    /*tab节点*/
    BASIC_MATERIALS("BASIC_MATERIALS","", "基础资料", 0, 0,"","","","","",null),
    INNER_OPERATION_MATERIALS("INNER_OPERATION_MATERIALS","", "内部操作资料", 0, 0,"","","","","",null),
    CONTRACT_MATERIALS("CONTRACT_MATERIALS","", "合同资料", 0, 0,"","","","","",null),
    LEASEHOLD_MATERIALS("LEASEHOLD_MATERIALS", "", "租赁物资料", 0, 0,"","","","","",null),
    COLLATERALIZATION_MATERIALS("COLLATERALIZATION_MATERIALS", "", "抵质押资料", 0, 0,"","","","","",null),

    REPORT("REPORT","INNER_OPERATION_MATERIALS", "立项审批单", 1, 1,"REFERENCE_MATERIALS","参考资料","PROJECT_ESTABLISH","立项上传资料","BUSINESS_PROJ_ESTABLISH","PROJ_ESTABLISH"),
    PROJ_INFORMATION("PROJ_INFORMATION","INNER_OPERATION_MATERIALS", "业务申请书", 2, 1,"REFERENCE_MATERIALS","参考资料","PROJECT_ESTABLISH","立项上传资料","BUSINESS_PROJ_ESTABLISH","PROJ_ESTABLISH"),
    PROJ_CREDIT_LETTER("PROJ_CREDIT_LETTER","INNER_OPERATION_MATERIALS", "征信授权书", 3, 1,"REFERENCE_MATERIALS","参考资料","PROJECT_ESTABLISH","立项上传资料","BUSINESS_PROJ_ESTABLISH","PROJ_ESTABLISH"),
    OTHER("OTHER","INNER_OPERATION_MATERIALS", "其他", 4, 1,"REFERENCE_MATERIALS","参考资料","PROJECT_ESTABLISH","立项上传资料","BUSINESS_PROJ_ESTABLISH","PROJ_ESTABLISH"),
    MEETING_MINUTES("MEETING_MINUTES","INNER_OPERATION_MATERIALS", "立项会会议纪要", 5, 1,"REFERENCE_MATERIALS","参考资料","PROJECT_ESTABLISH","立项上传资料","BUSINESS_PROJ_ESTABLISH","PROJ_ESTABLISH"),

    DUE_DILIGENCE_REPORT("DUE_DILIGENCE_REPORT","INNER_OPERATION_MATERIALS", "尽调报告", 1, 1,"REFERENCE_MATERIALS","参考资料","PROJECT_REVIEW","项目评审资料","BUSINESS_PROJ_REVIEW","PROJ_REVIEW"),
    RISK_REVIEW_REPORT("RISK_REVIEW_REPORT","INNER_OPERATION_MATERIALS", "审查报告", 20, 1,"REFERENCE_MATERIALS","参考资料","PROJECT_REVIEW","项目评审资料","BUSINESS_PROJ_REVIEW","PROJ_REVIEW"),

    LEGAL_COMPLIANCE_REPORT("LEGAL_COMPLIANCE_REPORT","INNER_OPERATION_MATERIALS", "法律合规意见书", 30, 1,"REFERENCE_MATERIALS","参考资料","PROJECT_REVIEW","项目评审资料","BUSINESS_PROJ_REVIEW","PROJ_REVIEW"),
    PROFESSIONAL_REVIEW_COMMENTS("PROFESSIONAL_REVIEW_COMMENTS","INNER_OPERATION_MATERIALS", "专职评审意见", 35, 1,"REFERENCE_MATERIALS","参考资料","PROJECT_REVIEW","项目评审资料","BUSINESS_PROJ_REVIEW","PROJ_REVIEW"),
    YIELD_REVIEW_REPORT("YIELD_REVIEW_REPORT","INNER_OPERATION_MATERIALS", "项目收益率审查意见书", 40, 1,"REFERENCE_MATERIALS","参考资料","PROJECT_REVIEW","项目评审资料","BUSINESS_PROJ_REVIEW","PROJ_REVIEW"),
    GMO_MEETING_MINUTES("GMO_MEETING_MINUTES","INNER_OPERATION_MATERIALS", "总经办会议纪要", 50, 1,"REFERENCE_MATERIALS","参考资料","PROJECT_REVIEW","项目评审资料","BUSINESS_PROJ_REVIEW","PROJ_REVIEW"),
    MEETING_REVIEW_REPORT("MEETING_REVIEW_REPORT","INNER_OPERATION_MATERIALS", "评审会会议纪要", 60, 1,"REFERENCE_MATERIALS","参考资料","PROJECT_REVIEW","项目评审资料","BUSINESS_PROJ_REVIEW","PROJ_REVIEW"),
    MEETING_REVIEW_RECORD("MEETING_REVIEW_RECORD","INNER_OPERATION_MATERIALS", "评审会会议记录", 70, 1,"REFERENCE_MATERIALS","参考资料","PROJECT_REVIEW","项目评审资料","BUSINESS_PROJ_REVIEW","PROJ_REVIEW"),
    DIRECTOR_MEETING_REPORT("DIRECTOR_MEETING_REPORT","INNER_OPERATION_MATERIALS", "董事会会议纪要", 80, 1,"REFERENCE_MATERIALS","参考资料","PROJECT_REVIEW","项目评审资料","BUSINESS_PROJ_REVIEW","PROJ_REVIEW"),

    REVIEW_OTHER("OTHER","INNER_OPERATION_MATERIALS", "其他", 90, 1,"REFERENCE_MATERIALS","参考资料","PROJECT_REVIEW","项目评审资料","BUSINESS_PROJ_REVIEW","PROJ_REVIEW"),
    ON_SITE_DUE_DILIGENCE("ON_SITE_DUE_DILIGENCE","INNER_OPERATION_MATERIALS", "现场尽调照片", 100, 1,"REFERENCE_MATERIALS","参考资料","PROJECT_REVIEW","项目评审资料","BUSINESS_PROJ_REVIEW","PROJ_REVIEW"),

    OPERATION_MANAGER_REVIEW_SUBMISSION("OPERATION_MANAGER_REVIEW_SUBMISSION","INNER_OPERATION_MATERIALS", "租赁物审核意见书", 1, 1,"REFERENCE_MATERIALS","参考资料","LEASEHOLD","租赁物资料","BUSINESS_LEASE_DATA_LIST","LEASE_DATA_LIST"),

    BUSINESS_PRICING_APPROVAL_FORM("BUSINESS_PRICING_APPROVAL_FORM","INNER_OPERATION_MATERIALS", "业务定价审批表", 1, 1,"REFERENCE_MATERIALS","参考资料","PROJECT_PRICING","项目定价资料","BUSINESS_PROJ_PRICING","PROJ_PRICING"),

    LOAN_REVIEW("LOAN_REVIEW","INNER_OPERATION_MATERIALS", "放款审核表", 1 ,null,"REFERENCE_MATERIALS","参考资料","PAYMENT","放款审核资料","BUSINESS_PAYMENT_LOAN_APPROVAL","PAYMENT"),
    PUBLIC_INFORMATION_QUERY("PUBLIC_INFORMATION_QUERY","INNER_OPERATION_MATERIALS", "公开信息查询资料", 1, null,"REFERENCE_MATERIALS","参考资料","PUBLIC_INFORMATION_QUERY","公开信息查询资料","BUSINESS_PUBLIC_INFORMATION_QUERY",null),
    APPROVE_SNAPSHOT("APPROVE_SNAPSHOT","INNER_OPERATION_MATERIALS", "审批快照", 1, null,"REFERENCE_MATERIALS","参考资料","APPROVE_SNAPSHOT","流程审批截图资料","BUSINESS_FLOW_SNAPSHOT",null),

    SIGN_PHOTO_VIDEO("SIGN_PHOTO_VIDEO","CONTRACT_MATERIALS", "合同签署照片和视频", 1, 1,"REFERENCE_MATERIALS","参考资料","CONTRACT","放款资料","BUSINESS_PAYMENT","PAYMENT"),

    LEASE_RELATED("LEASE_RELATED","LEASEHOLD_MATERIALS", "租赁物相关", 1, 1,"REFERENCE_MATERIALS","参考资料","LEASEHOLD_RELATE","租赁物相关资料","BUSINESS_LEASEHOLD","PAYMENT"),
    PUBLICITY_INFORMATION("PUBLICITY_INFORMATION","LEASEHOLD_MATERIALS", "全国企业信用信息公示系统、全国法院被执行人信息网及租赁物在中登网的查询情况", 3, 1,"REFERENCE_MATERIALS","参考资料","LEASEHOLD_RELATE","租赁物相关资料","BUSINESS_LEASEHOLD","PAYMENT"),

    POLICY("POLICY","LEASEHOLD_MATERIALS", "保单信息", 1, 1,"REFERENCE_MATERIALS","参考资料","POLICY","保险相关资料","BUSINESS_LEASEHOLD_POLICY","PAYMENTPOLICY"),

    ;

    private final String code;
    /** 父节点编码（空表示根节点） */
    private final String parentCode;
    /** 节点名称（前端展示） */
    private final String name;
    /** 同层级排序号（越小越靠前） */
    private final Integer sort;
    /** 节点层级（根=0，一级=1，二级=2...） */
    private final Integer level;
    /** 所属模块(资料参考/运营终审） */
    private final String moduleCode;
    /** 所属模块(资料参考/运营终审） */
    private final String moduleName;
    /** 所属列表(立项资料） */
    private final String groupCode;
    /**所属列表名称 */
    private final String groupName;
    /**所属businessType */
    private final String businessType;
    /**关联业务类型 */
    private final String relBusinessType;
    // 构造方法
    FilingDirectoryEnum(String code,String parentCode, String name, Integer sort, Integer level,
                        String moduleCode,String moduleName, String groupCode,String groupName,String businessType,String relBusinessType) {
        this.code = code;
        this.parentCode = parentCode;
        this.name = name;
        this.sort = sort;
        this.level = level;
        this.moduleCode = moduleCode;
        this.moduleName = moduleName;
        this.groupCode = groupCode;
        this.groupName = groupName;
        this.businessType = businessType;
        this.relBusinessType = relBusinessType;
    }

    // ========== 枚举辅助方法（核心：快速查询+构建树形） ==========
    /**
     * 根据父节点编码查询子节点（按排序号升序）
     */
    public static List<FilingDirectoryEnum> getChildrenByParentCode(String parentCode,String moduleCode) {
        return Arrays.stream(values())
                .filter(item -> Objects.equals(item.parentCode, parentCode))
                .filter(item -> Objects.equals(item.moduleCode, moduleCode))
                .sorted(Comparator.comparingInt(FilingDirectoryEnum::getSort))
                .collect(Collectors.toList());
    }
    public static List<FilingDirectoryEnum> getAllParent() {
        return Arrays.stream(values())
                .filter(item -> item.getParentCode() == null || item.getParentCode().isEmpty())
                .sorted(Comparator.comparingInt(FilingDirectoryEnum::getSort))
                .collect(Collectors.toList());
    }

    public static String getTabName(String code) {
        for (FilingDirectoryEnum value : FilingDirectoryEnum.values()) {
            if (value.code.equals(code)) {
                return value.getName();
            }
        }
        return null;
    }


    public String getParentCode() { return parentCode; }
    public String getName() { return name; }
    public Integer getSort() { return sort; }
    public Integer getLevel() { return level; }

    public String getCode() {
        return code;
    }

    public String getBusinessType() {
        return businessType;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getRelBusinessType() {
        return relBusinessType;
    }
}

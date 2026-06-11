package cn.zswltech.mithras.foundation.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 岗位枚举
 *
 * @author wangchuanhao
 * @date 2022/8/9 10:47 PM
 */
@AllArgsConstructor
@Getter
public enum JobEnum implements PullDown {

    /**
     *
     */
    admin("管理员"),
    leaderincharge("分管领导"),
    projmanager("项目经理"),
    riskmanager("风控经理"),
    chairman("董事长"),
    legalmanager("法务经理"),
    financialdirector("财务总监"),
    chiefriskofficer("首席风险官"),
    generalmanager("总经理"),
    deputygeneralmanager("副总经理"),
    director("董事"),
    boardsecretary("董事会秘书"),
    riskmember("风委会委员"),
    risksecretary("风委会秘书"),
    Jury("常任评审委员"),
    Jurydirector("常任评审委员主任"),
    expertlibrary("非常任评审委员"),
    fullreviewcommittee("专职评审委员"),
    assetclassifyjury("评审委员（资产五级分类用）"),
    secretaryjury("评审会秘书"),
    riskdeptmanager("风管部负责人"),
    assetmanagement("资产管理"),
    loanreviewpost("放款审核岗"),
    financialofficer("财务主管"),
    financialmanager("财务经理"),
    cashier("出纳"),
    moneymanager("资金经理"),
    deepmoneymanager("资深资金经理"),
    moneymanagerhead("资金业务负责人"),
    moneymanagerleader("资金分管领导"),
    compremanager("综合部负责人"),
    compreleader("综合部分管领导"),
    Informationpost("信息岗"),
    businesshead("业务负责人"),
    officesecretary("总经办秘书"),
    pricingcommitteesecretary("定价委员会会秘书"),
    pricingcommitteemember("定价委员会委员"),
    pricingcommitteedirector("定价委员会主任"),
    archivesmanage("档案管理岗"),
    kpimanagement("绩效考核岗"),
    operationmanagementagent("运营管理部经办人"),
    headoflegalcompliance("法律合规部负责人"),
    yunYingGuanLi("运营管理（经办）"),
    yunYingGuanLiReview("运营管理（复核）"),
    yinZhangGuanLi("印章管理"),
    operationManagement("运营经理（经办）"),
    operationManagementReview("运营经理（复核）"),
    headofyyglb("运营管理部负责人"),
    yyglbleader("运营管理部分管领导"),
    InformationTechnologyPost("信科岗"),
    teamleader("团队长"),
    humanresourcessupervisor("人力资源部负责人"),
    humanresourcesleader("人力资源部分管领导"),
    comprehensiveDept("综合部经办"),
    chairmanauthorization("董事长授权岗"),
    assetManagementReview("资产管理复核岗"),
    creditsearcher("征信查询员"),

    ;
    private String display;

    private static Map<String, JobEnum> map;

    static {
        map = Stream.of(JobEnum.values()).collect(Collectors.toMap(JobEnum::name, e -> e));
    }

    public static JobEnum getByName(String name) {
        return map.get(name);
    }

    @Override
    public String display() {
        return display;
    }
}

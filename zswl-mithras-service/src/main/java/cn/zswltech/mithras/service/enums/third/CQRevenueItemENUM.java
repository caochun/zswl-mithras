package cn.zswltech.mithras.service.enums.third;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * 苍穹系统应收单-收入项目枚举
 **/
@Getter
public enum CQRevenueItemENUM {
    SX006("商品销售收入人", "-"),
    SX025("动产经营租赁收入人", "业务类型是经营性租赁，不区分现金流项目"),
    SX032("其他租赁收入人", "-"),
    SX046("违约及赔款收入", "现金流项目=罚息 或 提前终止补偿金"),
    SX049("其他收益", "保证金抵扣时，对应该项目，金额为负数"),
    SX110("动产经营租赁本金收入", "业务类型=直租，现金流项目=本金"),
    SX111("动产经营租赁利息收入", "业务类型=直租，现金流项目=利息"),
    SX112("动产融资租赁首期租金收入", "业务类型=直租，现金流项目=首期租金"),
    SX113("动产融资租赁留购价款收入", "业务类型=直租，现金流项目=名义价款"),
    SX114("动产融资租赁手续费收入", "业务类型=直租，现金流项目=手续费"),
    SX115("动产融资租赁咨询费收入", "业务类型=直租，现金流项目=咨询费"),
    SX116("动产售后回租本金收入", "业务类型=回租，现金流项目=本金"),
    SX117("动产售后回租利息收入", "业务类型=回租，现金流项目=利息"),
    SX118("动产售后回租首期租金收入", "业务类型=回租，现金流项目=首期租金"),
    SX119("动产售后回租留购价款收入", "业务类型=回租，现金流项目=名义价款"),
    SX120("动产售后回租手续费收入", "业务类型=回租，现金流项目=手续费"),
    SX121("动产售后回租咨询费收入", "业务类型=回租，现金流项目=咨询费"),
    SX122("动产转租赁本金收入", "业务类型=转租赁，现金流项目=本金"),
    SX123("动产转租赁利息收入", "业务类型=转租赁，现金流项目=利息"),
    SX124("动产转租赁首期租金收入", "业务类型=转租赁，现金流项目=首期租金"),
    SX125("动产转租赁留购价款收入", "业务类型=转租赁，现金流项目=名义价款"),
    SX126("动产转租赁手续费收入", "业务类型=转租赁，现金流项目=手续费"),
    SX127("动产转租赁咨询费收入", "业务类型=转租赁，现金流项目=咨询费"),
    SX128("商业保理本金收入", "业务类型=保理，现金流项目=本金"),
    SX129("商业保理利息收入", "业务类型=保理，现金流项目=利息"),
    SX130("商业保理手续费收入", "业务类型=保理，现金流项目=手续费"),
    SX131("商业保理咨询费收入", "业务类型=保理，现金流项目=咨询费");

    private final String description;
    private final String adaptScene;

    CQRevenueItemENUM(String description,String adaptScene){
        this.description = description;
        this.adaptScene = adaptScene;
    }

    private static final Map<String, CQRevenueItemENUM> map = Stream.of(CQRevenueItemENUM.values()).collect(Collectors.toMap(CQRevenueItemENUM::name, e -> e, (a, b) -> a));

    public static CQRevenueItemENUM of(String name) {
        return map.get(name);
    }

    public static CQRevenueItemENUM getCQRevenueByBusiness(CashFlowItemEnum cashFlowItem, ProjectBizType bizType, LeaseType leaseType, boolean isPrincipal){
        if(!ObjectUtil.isAllNotEmpty(cashFlowItem, bizType)){
            return null;
        }
        //经营性租赁
        if(LeaseType.jyx_zu.equals(leaseType)){
            return SX025;
        }
        //直租
        if (LeaseType.zhi_zu.equals(leaseType)) {
            switch (cashFlowItem) {
                case FIRST_RENT:
                    return SX112;
                case OTHERAMOUNT:
                    return SX115;
                case RENT:
                    if (isPrincipal) {
                        return SX110;
                    } else {
                        return SX111;
                    }
                case COMMISSION:
                    return SX114;
                case NOMINAL_PRICE:
                    return SX113;
                case EARLY_STOP_COMPENSATION:
                    return SX046;
                case FIRST_INSTALLMENT_INTEREST:
                    return SX111;
                case LEASE_RENT:
                    return SX110;
                default:
                    return SX049;
            }
        }

        //回租
        if (LeaseType.hui_zu.equals(leaseType)) {
            switch (cashFlowItem) {
                case FIRST_RENT:
                    return SX118;
                case OTHERAMOUNT:
                    return SX121;
                case RENT:
                    if (isPrincipal) {
                        return SX116;
                    } else {
                        return SX117;
                    }
                case COMMISSION:
                    return SX120;
                case NOMINAL_PRICE:
                    return SX119;
                case EARLY_STOP_COMPENSATION:
                    return SX046;
                case FIRST_INSTALLMENT_INTEREST:
                    return SX111;
                case LEASE_RENT:
                    return SX110;
                default:
                    return SX049;
            }
        }

        if(ProjectBizType.ZZ.equals(bizType)){
            switch (cashFlowItem) {
                case FIRST_RENT:
                    return SX124;
                case OTHERAMOUNT:
                    return SX127;
                case RENT:
                    if (isPrincipal) {
                        return SX122;
                    } else {
                        return SX123;
                    }
                case COMMISSION:
                    return SX126;
                case NOMINAL_PRICE:
                    return SX125;
                case EARLY_STOP_COMPENSATION:
                    return SX046;
                case FIRST_INSTALLMENT_INTEREST:
                    return SX111;
                case LEASE_RENT:
                    return SX110;
                default:
                    return SX049;
            }
        }
        ///保理
        if(ProjectBizType.BL.equals(bizType)){
            switch (cashFlowItem) {
                case OTHERAMOUNT:
                    return SX131;
                case RENT:
                    if (isPrincipal) {
                        return SX128;
                    } else {
                        return SX129;
                    }
                case COMMISSION:
                    return SX130;
                case EARLY_STOP_COMPENSATION:
                    return SX046;
                case FIRST_INSTALLMENT_INTEREST:
                    return SX111;
                case LEASE_RENT:
                    return SX110;
                default:
                    return SX049;
            }
        }
        return null;
    }

}
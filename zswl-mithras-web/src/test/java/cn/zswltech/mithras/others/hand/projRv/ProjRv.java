package cn.zswltech.mithras.others.hand.projRv;

import lombok.Data;

import java.util.List;

/**
 * @author junke
 */
@Data
public class ProjRv {

    private String jsFunction;
    private 列表信息 列表信息;
    private 基本信息 基本信息;
    private List<客户信息> 客户信息List;
    private 报价信息 报价信息;
    private List<现金流信息> 现金流信息List;

    @Data
    public static class 现金流信息 {
        private String 应收日期;
        private String 期数;
        private String 现金流类型;
        private String 现金流项目;
        private String 应收金额;
        private String 本金;
        private String 利息;
        private String 当期剩余本金;
    }

    @Data
    public static class 报价信息 {
        private String 融资金额_元;
        private String 报价方案;
        private String 币种;
        private String 起租日;
        private String 租赁期限_月;
        private String 结束日;
        private String 投放日期;
        private String 利率类型;
        private String 租赁期数;
        private String LPR利率类型;
        private String LPR利率;
        private String 幅度;
        private String 租赁利率;
        private String 保证比例;
        private String 保证金_元;
        private String 租赁咨询费_元;
        private String 租赁咨询费比例;
        private String 名义货价_元;
        private String 租金支付频率;
        private String 每期租金;
        private String IRR;
        private String XIRR;
        private String 授信金额_元;
        private String 罚息日利率;
        private String 额度类型;
        private String 风险敞口;
        private String 还款方式;
        private String 其他还款保证方式;
    }


    @Data
    public static class 客户信息 {
        private String 客户名称;
        private String 客户分类;
        private String 客户类别;
        private String 客户类型;
        private String 备注;
    }


    @Data
    public static class 基本信息 {
        private String 立项编号;
        private String 立项名称;
        private String 项目编号;
        private String 项目名称;
        private String 承租人名称;
        private String 单据类型;
        private String 业务类型;
        private String 业务主办;
        private String 业务协办一;
        private String 业务协办二;
        private String 业务部;
        private String 预计起租日;
        private String 预测算XIRR;
        private String 行业;
        private String 资产类别;
        private String 产品线;
        private String 项目类型;
        private String 资金用途;
        private String 风控措施_担保;
        private String 风控措施_抵押;
        private String 风控措施_质押;
        private String 风控措施_其他;
        private String 历史合作情况;
        private String 其他情况说明;
    }

    @Data
    public static class 列表信息 {
        private String 项目编号;
        private String 项目名称;
        private String 承租人名称;
        private String 项目类型;
        private String 项目经理;
        private String 部门负责人;
        private String 业务部;
        private String 创建日期;
        private String 项目状态;
//        private String 立项编号;
    }

}



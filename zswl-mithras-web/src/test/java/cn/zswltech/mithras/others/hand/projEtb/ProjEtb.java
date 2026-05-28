package cn.zswltech.mithras.others.hand.projEtb;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author junke
 */
@Data
public class ProjEtb {
    //其他
    private volatile String jsFunction;
    //首页列表行信息
    private 列表信息 列表信息;
    //详情页面
    private String 项目概述;
    private 基本信息 基本信息;
    private List<客户信息> 客户信息列表 = new ArrayList<>();
    private List<现金流信息> 现金流信息列表;
    private List<资料清单> 资料清单列表;
    private List<附件> 附件列表;

    private 租赁方案 租赁方案;
    private 租赁物概况 租赁物概况;
    private 其他风险情况 其他风险情况;

    @Data
    public static class 列表信息 {
        private String 立项编号;
        private String 立项名称;
        private String 业务经理;
        private String 业务部;
        private String 业务类型;
        private String 产品线;
        private String 客户名称;
        private String 预计融资金额;
        private String 创建日期;
        private String 状态;
    }

    @Data
    public static class 附件 extends 资料清单 {
        private String 说明;
    }

    @Data
    public static class 资料清单 {
        private String 文件名称;
        private Map<String, List<String>> 附件map = new HashMap<>();
        private String 附件名称;
        private String 归档类别;
        private String 备注;

    }

    @Data
    public static class 其他风险情况 {
        private String 项目情况;
        private String 资金用途;
        private String 担保措施;
        private String 其它风险缓释措施;
        private String 还款来源;
        private String 项目亮点;
        private String 项目风险及防范措施;
    }

    @Data
    public static class 租赁物概况 {
        private String 租赁物概况;
        private String 租赁物价值;
        private String 租赁物权属;
        private String 租赁物保险;
    }

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
    public static class 基本信息 {
        private String 立项名称;
        private String 立项编号;
        private String 业务类型;
        private String 客户名称;
        private String 业务经理;
        private String 协办经理;
        private String 部门;
        private String 单据类型;
        private String 产品线;
        private String 供应商;
        private String 关联合同编号;
        private String 项目来源;
    }

    @Data
    public static class 客户信息 {
        private String 客户编号;
        private String 客户类型;
        private String 客户名称;
    }

    @Data
    public static class 租赁方案 {
        private String 价目表;
        private String 净投放额;
        private String 租赁期限;
        private String 租金偿还方式;
        private String 融资金额;
        private String 首期租金;
        private String 咨询费;
        private String 其他费用;
        private String 保证金;
        private String 租赁利率;
        private String 租赁利率类型;
        private String 留购价款;
        private String 支付频率;
        private String 还租期数;
        private String 每期租金;
        private String 首期还款日;
        private String 内涵报酬率XIRR;
        private String 预计内涵报酬率XIRR;
        private String 起租日;
        private String 说明;
    }


}

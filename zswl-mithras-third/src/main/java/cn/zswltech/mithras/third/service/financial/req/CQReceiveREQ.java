package cn.zswltech.mithras.third.service.financial.req;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName ReceiveVo
 * @Description 苍穹应收单
 * @Author jackerhe
 * @Date 2022/10/21 4:06 下午
 * @Version 1.0
 **/
@Data
public class CQReceiveREQ extends CQBaseREQ{

    //管理公司财务系统编码
    private String org;

    //默认‘ar_finarbill_BT_zb’
    //单据类型 默认应收单（总部）
    private String billtype = "ar_finarbill_BT_zb";

    //客户
    private String asstact;

    //来源系统 默认融租易系统
    private String cico_srcsystem = "RZY";

    //合同号 contractCode
    private String cico_contractnumun;

    //到期日期 2022.6.26
    private String maturitydate;

    //结算方式
    //private String settletype;


    private List<ReceiveBody> entry;

    @Data
    public class ReceiveBody {

        //单据体.收入项目 1001	一般销售
        //1002	退货折让
        //1003	代收业务
        //1004	暂付应收
        //SX006	商品销售收入
        //SX032	其他租赁收入
        //SX049	其他收益
        //SX050	营业外收入
        //SX073	融资租赁租金收款
        //SX074	融资租赁手续费收入
        //SX075	商业保理本金收款
        //SX076	商业保理利息收入
        //SX077	售后回租手续费收入
        private String cico_incomeitems;

        private String ftaxunitprice;

        //应收金额
        private BigDecimal e_receivableamt;
        //是否开票
        private String cico_isinvoice;

        private String axrate;

        //录入含税价 默认打开
        private String isincludetax;

        //单据体.物料名称
        private String e_materialname;


/*
        //单据体.开票名称
        private String e_material;

        //单据体.商品名称
        private String cico_goods;

        //单据体.税收分类
        private String cico_taxclassification;

        //单据体.规格型号
        private String e_spectype;

        //单据体.计量单位
        private String e_measureunit;

        //单据体.数量
        private String e_quantity;

        //单据体.单价
        private String e_unitprice;

        //单据体.税率
        private String taxrateid;

        //单据体.税额
        private String e_tax;

        //单据体.含税单价
        private String e_taxunitprice;

        //单据体.不含税金额
        private String e_amount;

        //发票类型
        private String cico_invoicetype;

        //电子发票收票邮箱
        private String cico_invoiceemail;

        //货种
        private String cico_commodity;*/

    }

}

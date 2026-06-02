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
public class CQPaymentREQ extends CQBaseREQ{

    //管理公司财务系统编码
    private String org;

    //默认‘ar_finarbill_BT_zb’
    //单据类型 默认应收单（总部）
    private String billtype = "ar_finarbill_BT_zb";

    //情况说明
    private String remark;

    //来源系统 默认融租易系统
    private String cico_srcsystem = "RZY";

    //合同号 contractCode
    private String cico_contractnumun;

    //客户 情况说明
    private String asstact;

    //收款人银行账号
    private String payeebanknum;


    private List<PaymentBody> entry;

    @Data
    public class PaymentBody {

        //应付金额
        private BigDecimal e_pricetaxtotal;

    }

}

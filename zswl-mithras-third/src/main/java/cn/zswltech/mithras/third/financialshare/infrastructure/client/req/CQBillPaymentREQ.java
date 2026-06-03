package cn.zswltech.mithras.third.financialshare.infrastructure.client.req;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName ReceiveVo
 * @Description 苍穹付款申请
 * @Author jackerhe
 * @Date 2022/10/21 4:06 下午
 * @Version 1.0
 **/
@Data
public class CQBillPaymentREQ extends CQBaseREQ{

    //申请组织
    private String settleorg;

    //默认‘ar_finarbill_BT_zb’
    //单据类型 默认应收单（总部）
    private String billtype = "ar_finarbill_BT_zb";

    //合同号 contractCode
    private String cico_contractnumun;


    //来源系统 默认融租易系统
    private String cico_srcsystem = "RZY";

    //是否包含首期租金
    private Integer isinitialrent;

    //首期租金金额
    private BigDecimal initialrentamount;

    //内扣质保金
    private BigDecimal cico_warrantyamount;

    private List<PaymentBody> entry;

    @Data
    public class PaymentBody {

        //收款单位名称
        private String e_assacct;

        //收款单位银行账号
        private String e_bebank;

        //付款类型（区分保证金退款，付款)
        private String e_paymenttype;

        //申请支付金额
        private BigDecimal e_applyamount;

    }

}

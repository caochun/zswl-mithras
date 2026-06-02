package cn.zswltech.mithras.third.service.financial.req;

import lombok.Data;

import java.util.List;

/**
 * @ClassName CQRefund
 * @Description
 * @Author jackerhe
 * @Date 2022/11/1 1:57 下午
 * @Version 1.0
 **/
@Data
public class CQRefundREQ extends CQBaseREQ{

    //默认‘ar_finarbill_BT_zb’
    //单据类型 默认应收单（总部）
    private String billtype = "ar_finarbill_BT_zb";

    //合同号 contractCode
    private String cico_contractnumun;

    private List<RefundBody> entry;

    @Data
    public class RefundBody{

        //收款单位名称
        private String e_assacct;

        //收款单位银行账号
        private String e_bebank;

        //付款类型
        private String e_paymenttype;

        //申请支付金额
        private String e_applyamount;

    }

}

package cn.zswltech.mithras.service.service.third.financial.req;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName AppTokenREQ
 * @Description
 * @Author jackerhe
 * @Date 2022/10/28 11:14 上午
 * @Version 1.0
 **/
@Data
public class ReceiveProvisionREQ {

    //租赁固定10000396
    private String tallycompany = "10000396";

    private String company = "10000396";

    private String bizdate;

    private String dept;

    //验重使用
    private String sourcebillno;

    private List<ReceiveProvisionBody> entry;

    @Data
    public class ReceiveProvisionBody{

        private Long receiptId;

        private String ywlxtype;

        private String cico_contract_num;

        //记账金额
        private BigDecimal tallyamount;
    }

}

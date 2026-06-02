package cn.zswltech.mithras.third.service.br.rsp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BRFlowHistoryRsp extends BRCommonRsp {

    private BRFlowHistoryBody body;

    @Data
    public static class BRFlowHistoryBody {
        private List<Transaction> list;

        private String _totalRow;

    }

    @Data
    public static class Transaction {
        private int RN;
        private String BRUID;
        private String ORG_NAME;
        private String ACCOUNTNUMBER;
        private String TRANSSEQ;
        private String TRADEDATETIME;
        private String TRADEDATE;
        private String TRADETIME;
        private String QIXIRIQI;
        private String MONEYWAY;
        private BigDecimal AMOUNT;
        private BigDecimal CURRENTBALANCE;
        private String LASTMODIFIEDON;
        private String CHECKCODE;
        private String PURPOSE;
        private String COMMENTS;
        private String OPPOSITEACCOUNTNUMBER;
        private String OPPOSITEACCOUNTNAME;
        private String OPPOSITEBANK;
        private String BILLCODE;
        private String BILLTYPE;
        private String CHECKBATCHNO;
        private String BANKSERIALNUMBER;
        private String NOTECODE;
        private String BANKBUSREF;
        private String RECEIPTCODE;
        private String RECEIPTBUSTYPNO;
        private String RECEIPTINFO;
        private String BUSREF;
    }


}
package cn.zswltech.mithras.third.financialshare.client.resp;

import lombok.Data;

import java.util.List;

/**
 * @ClassName CommonRSP
 * @Description 苍穹基础返回
 * @Author jackerhe
 * @Date 2022/10/26 2:44 下午
 * @Version 1.0
 **/
@Data
public class FinancialCommonRSP extends FinancialBaseRSP{

    private List<FinancialRSPBody> data;

    @Data
    public class FinancialRSPBody{

        private Boolean success;

        //苍穹应收/应付单单号
        private String billno;

        //来源单据编号
        private String sourcebillno;

        private String msg;

    }

}

package cn.zswltech.mithras.third.financialshare.client.req;

import lombok.Data;

/**
 * @ClassName CQWithdrawREQ
 * @Description 撤回接口
 * @Author jackerhe
 * @Date 2022/11/1 2:29 下午
 * @Version 1.0
 **/
@Data
public class CQWithdrawREQ {

        //来源单据编号
        private String sourcebillno;

        //单据类型：A：应收单；B：应付单；C：付款申请单
        private String billtype;
}

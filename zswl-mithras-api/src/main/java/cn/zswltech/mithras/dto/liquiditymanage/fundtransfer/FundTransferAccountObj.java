package cn.zswltech.mithras.dto.liquiditymanage.fundtransfer;

import lombok.Data;



/**
 * AccountBalanceListREQ
 *
 * @author zhouning
 * @since 2024/12/24
 */
@Data
public class FundTransferAccountObj extends FundTransferObj{
    /**
     * 银行账号
     */
    private String accountNumber;

    /**
     * 银行名称
     */
    private String accountBank;

}

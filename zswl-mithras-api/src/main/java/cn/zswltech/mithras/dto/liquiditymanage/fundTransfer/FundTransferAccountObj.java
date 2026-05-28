package cn.zswltech.mithras.dto.liquiditymanage.fundTransfer;

import lombok.Data;

import java.time.LocalDate;


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

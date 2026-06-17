package cn.zswltech.mithras.liquidity.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Liquidity account input converted from the account master data domain.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidityBankAccountSnapshot {

    private Long id;

    private String accountType;

    private String accountName;

    private String accountNumber;

    private String accountBank;
}

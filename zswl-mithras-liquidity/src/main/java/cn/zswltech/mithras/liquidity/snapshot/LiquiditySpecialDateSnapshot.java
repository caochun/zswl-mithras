package cn.zswltech.mithras.liquidity.snapshot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Liquidity calendar input converted from the base data domain.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquiditySpecialDateSnapshot {

    private LocalDate specialDate;

    private String specialType;
}

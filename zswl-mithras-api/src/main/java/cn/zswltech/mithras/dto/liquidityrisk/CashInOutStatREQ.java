package cn.zswltech.mithras.dto.liquidityrisk;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author luyi
 */
@Data
public class CashInOutStatREQ {

    /**
     * 日期开始
     */
    @NotNull
    private LocalDate dateFrom;

    /**
     * 日期结束
     */
    @NotNull
    private LocalDate dateTo;

    /**
     * 金额不足
     */
    private Boolean lackBalance;

    /**
     * 日期错配
     */
    private Boolean mismatchBalance;
}

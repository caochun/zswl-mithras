package cn.zswltech.mithras.service.mapper.lib.fund.financing;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/4/24 19:06
 */
@Data
public class FinancingQueryDto {

    private List<String> timeLimitType;

    private List<String> businessType;

    private LocalDate endDate;

    private LocalDate startDate;

    private LocalDate actualLoanDateTo;

    /**
     * 排除掉结清，不为空则生效
     */
    private Integer excludeSettle;
}

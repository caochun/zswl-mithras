package cn.zswltech.mithras.service.service.fund.receiptrepay;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/21 10:17
 */
@Data
@Accessors(chain = true)
public class FundReceiptRepayListQueryDto {
    private Long financingOrgId;
    private String receiptRepayCode;
    private Long financingAmountFrom;
    private Long financingAmountTo;
    private String receiptRepayState;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private Long fundManager;
    private LocalDateTime createTimeFrom;
    private LocalDateTime createTimeTo;
    private LocalDateTime updateTimeFrom;
    private LocalDateTime updateTimeTo;
    private List<String> financingBizType;

}

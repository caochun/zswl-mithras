package cn.zswltech.mithras.fund.application.receiptrepay.dto;

import lombok.Data;

/**
 * @author dingqi
 * @date 2024/6/12
 * @description
 */
@Data
public class FundReceiptBaseRichDTO {
    private Long id;
    private String financingName;
    private Long financingAmount;
    private String financingCode;
    private Long financingId;
}

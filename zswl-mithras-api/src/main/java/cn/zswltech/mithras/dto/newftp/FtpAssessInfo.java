package cn.zswltech.mithras.dto.newftp;

import cn.zswltech.mithras.api.payment.dto.PaymentWrittenOffAmountRsp;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/3/11
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FtpAssessInfo extends PaymentWrittenOffAmountRsp.FtpAssessDTO {
    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("借据id")
    private Long receiptId;

    @ApiModelProperty("借据编号")
    private String receiptCode;

    @ApiModelProperty("FTP计息变更开始日期")
    private LocalDate effectDate;

    @ApiModelProperty("FTP计息变更差额调整日期")
    private LocalDate ftpInterestDiffDate;
}

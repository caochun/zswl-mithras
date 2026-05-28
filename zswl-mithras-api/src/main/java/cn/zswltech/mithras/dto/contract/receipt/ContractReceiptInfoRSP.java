package cn.zswltech.mithras.dto.contract.receipt;

import cn.zswltech.mithras.api.payment.dto.PaymentWrittenOffAmountRsp;
import cn.zswltech.mithras.dto.newftp.FtpAssessInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2025/3/13
 * @description
 */
@Data
public class ContractReceiptInfoRSP {
    @ApiModelProperty("借据id")
    private Long id;
    @ApiModelProperty("借据编号")
    private String receiptCode;
    @ApiModelProperty("最新生效的FTP考核信息")
    private FtpAssessInfo ftpAssessInfo;
}

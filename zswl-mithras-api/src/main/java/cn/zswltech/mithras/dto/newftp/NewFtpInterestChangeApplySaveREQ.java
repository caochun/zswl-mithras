package cn.zswltech.mithras.dto.newftp;

import cn.zswltech.mithras.api.payment.dto.PaymentWrittenOffAmountRsp;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2025/3/4
 * @description
 */
@Data
public class NewFtpInterestChangeApplySaveREQ {
    @ApiModelProperty("FTP计息变更申请记录id")
    private Long id;

    @ApiModelProperty("FTP考核信息")
    @NotEmpty(message = "至少需要一条FTP考核信息")
    @Valid
    private List<FtpAssessInfo> ftpAssessmentInfoList;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class FtpAssessInfo extends PaymentWrittenOffAmountRsp.FtpAssessDTO {
        @NotNull(message = "<借据id>不能为空")
        @ApiModelProperty("借据id")
        private Long receiptId;

        @NotNull(message = "<FTP计息变更开始日期>不能为空")
        @ApiModelProperty("FTP计息变更开始日期，yyyy-MM-dd")
        private LocalDate effectDate;

        @NotNull(message = "<FTP计息变更差额调整日期>不能为空")
        @ApiModelProperty("FTP计息变更差额调整日期，yyyy-MM-dd")
        private LocalDate ftpInterestDiffDate;
    }
}

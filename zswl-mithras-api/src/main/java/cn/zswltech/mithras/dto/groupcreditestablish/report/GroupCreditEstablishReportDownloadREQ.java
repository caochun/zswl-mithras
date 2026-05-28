package cn.zswltech.mithras.dto.groupcreditestablish.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author luyi
 */
@Data
@ApiModel("资料清单下载-请求体")
public class GroupCreditEstablishReportDownloadREQ {
    @NotNull
    @ApiModelProperty("记录id")
    private Long id;
}

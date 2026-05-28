package cn.zswltech.mithras.dto.projestablish.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author luyi
 */
@Data
@ApiModel("资料清单下载-请求体")
public class ProjEstablishReportDownloadREQ {
    @NotNull
    @ApiModelProperty("记录id")
    private Long recordId;
}

package cn.zswltech.mithras.dto.projestablish.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author luyi
 */
@ApiModel("立项报告删除删除-请求体")
@Data
public class ProjEstablishReportRemoveREQ {

    @NotNull
    @ApiModelProperty(value = "id", required = true)
    private Long id;
}

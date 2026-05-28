package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("租后报告清单列表-请求体")
public class AfterLeaseSingleTypeReportListREQ {
    @NotNull(message = "租后项目评审记录id不能为空")
    @ApiModelProperty("租后项目评审记录id")
    private Long adjustId;

    @NotBlank(message = "资料类型不能为空")
    @ApiModelProperty("资料类型")
    private String materialsType;
}

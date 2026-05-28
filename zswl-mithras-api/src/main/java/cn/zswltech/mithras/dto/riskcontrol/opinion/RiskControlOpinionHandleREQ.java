package cn.zswltech.mithras.dto.riskcontrol.opinion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author yibin
 */
@Data
@ApiModel("舆情处置")
public class RiskControlOpinionHandleREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

    @NotBlank
    @Length(min = 2, max = 10000)
    @ApiModelProperty("处置意见")
    private String advisement;

    @ApiModelProperty("流程id")
    private String processInstanceId;

    @ApiModelProperty("处置方式 0 处理， 1 关闭")
    private Integer handleResult;


}

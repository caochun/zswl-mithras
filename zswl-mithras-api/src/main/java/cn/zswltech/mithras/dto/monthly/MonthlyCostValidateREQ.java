package cn.zswltech.mithras.dto.monthly;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("成本计提-校验-参数")
public class MonthlyCostValidateREQ {

    @NotBlank(message = "月份不得为空")
    @ApiModelProperty(value = "处理月份 yyyy-MM")
    private String yearAndMonth;
}

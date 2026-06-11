package cn.zswltech.mithras.dto.interestpay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("提交入参")
public class InterestPayREQ {

    //@NotBlank(message = "起始月份不得为空")
    @ApiModelProperty(value = "起始月份 yyyy-MM")
    private String startYearAndMonth;

    //@NotBlank(message = "结束月份不得为空")
    @ApiModelProperty(value = "结束月份 yyyy-MM")
    private String endYearAndMonth;

}

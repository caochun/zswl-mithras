package cn.zswltech.mithras.dto.monthly;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
@ApiModel("列表-参数")
public class MonthlyAddREQ extends PageReq {

    @NotBlank(message = "开始时间不得为空")
    @ApiModelProperty(value = "yyyy-MM")
    private String yearAndMonth;

}

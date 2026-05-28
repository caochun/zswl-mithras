package cn.zswltech.mithras.dto.fund;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("授信列表-请求体")
@AllArgsConstructor
@NoArgsConstructor
public class FundCreditSingletonIdREQ extends PageReq {

    @NotNull(message = "授信id不得为空")
    @ApiModelProperty(value = "授信id")
    private Long id;

}

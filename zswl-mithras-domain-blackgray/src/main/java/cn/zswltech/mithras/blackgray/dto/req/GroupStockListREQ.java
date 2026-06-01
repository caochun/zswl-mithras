package cn.zswltech.mithras.blackgray.dto.req;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupStockListREQ extends PageReq {
    @NotBlank
    @ApiModelProperty(value = "集团名称")
    private String groupName;

    /**
     * 业务类型
     */
    @ApiModelProperty(value = "业务类型")
    @NotBlank
    private String businessType;
}

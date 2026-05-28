package cn.zswltech.mithras.dto.creditreport;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("项目管理-征信报告查询列表-请求参数")
public class CreditSearchProjectQuery extends PageReq {

    @NotNull(message = "id不能为空")
    @ApiModelProperty("项目id")
    private Long projectId;

    @NotBlank(message = "id类型不能为空")
    @ApiModelProperty("id业务类型")
    private String projIdDataType;

}

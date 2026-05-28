package cn.zswltech.mithras.dto.process.modify.remark;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author yibin
 */

@Data
@ApiModel("变更流程-附加标记信息-详情-请求体")
public class ProcessModifyRemarkDetailREQ {


    @NotBlank
    @ApiModelProperty("模块类型")
    private String moduleType;

    /**
     * main_id
     */
    @NotNull
    @ApiModelProperty("主表id")
    private Long mainId;

    @NotBlank
    @ApiModelProperty("标记类型")
    private String remarkType;

}

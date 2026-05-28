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
@ApiModel("变更流程-附加标记信息-详情-返回体")
public class ProcessModifyRemarkDetailRSP {

    private Long id;

    @ApiModelProperty("模块类型")
    private String moduleType;

    /**
     * remark_type
     */
    @NotBlank
    @ApiModelProperty("变更流程标记信息类型")
    private String remarkType;

    /**
     * remark_json
     */
    @NotNull
    @ApiModelProperty("标记信息")
    private ProcessModifyObjDTO remarkJson;

    /**
     * main_id
     */
    @NotNull
    @ApiModelProperty("主表id")
    private Long mainId;
}

package cn.zswltech.mithras.dto.process.modify.remark;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yibin
 */

@Data
@ApiModel("变更流程-附加标记信息-所有-返回体")
public class ProcessModifyRemarkAllRSP {

    @ApiModelProperty("变更流程-附加标记信息id")
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
    @ApiModelProperty("标记信息列表")
    private List<ProcessModifyObjDTO> remarkJsonList = new ArrayList<>();

    /**
     * main_id
     */
    @NotNull
    @ApiModelProperty("主表id")
    private Long mainId;
}

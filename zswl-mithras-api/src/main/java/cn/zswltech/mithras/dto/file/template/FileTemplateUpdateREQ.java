package cn.zswltech.mithras.dto.file.template;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 * @date 2024/12/26 10:12
 * @description
 */
@Data
public class FileTemplateUpdateREQ {

    @ApiModelProperty("id")
    @NotNull(message = "id不能为空")
    private Long id;


    /**
     * 合同面签是否需要展示 0：不需要 1：需要
     */
    @ApiModelProperty(value = "合同面签是否需要展示")
    private Integer faceSignShowFlag;

}

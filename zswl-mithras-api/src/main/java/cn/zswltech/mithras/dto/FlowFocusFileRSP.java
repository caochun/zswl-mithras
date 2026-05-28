package cn.zswltech.mithras.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dingqi
 * @date 2024/2/5
 * @description
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel("审批流-重点关注文件-返回体")
public class FlowFocusFileRSP {
    @ApiModelProperty("文件id")
    private Long fileId;

    @ApiModelProperty("文件名称")
    private String fileName;

    @ApiModelProperty("文件id类型")
    private Integer idType;
}

package cn.zswltech.mithras.dto.flow.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 模型列表查询
 *
 * @author wangchuanhao
 * @date 2022/10/25 2:01 PM
 */
@Data
public class ModelListRSP {

    /**
     * 模型id
     */
    @ApiModelProperty("模型id")
    private String modelId;

    /**
     * 模型标识
     */
    @ApiModelProperty("模型标识")
    private String modelKey;

    /**
     * 模型名称
     */
    @ApiModelProperty("模型名称")
    private String modelName;

    /**
     * 版本号
     */
    @ApiModelProperty("版本号")
    private Integer version;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private LocalDateTime updateTime;

    /**
     * 发布id
     */
    @ApiModelProperty("发布id")
    private String deploymentId;

    /**
     * 是否发布
     */
    @ApiModelProperty("是否发布")
    private Boolean deployFlag;

}

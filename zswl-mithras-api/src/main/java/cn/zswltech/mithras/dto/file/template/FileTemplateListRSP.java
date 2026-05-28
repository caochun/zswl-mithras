package cn.zswltech.mithras.dto.file.template;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yibin
 */
@Data
public class FileTemplateListRSP {
    @ApiModelProperty("模板文件id")
    private Long id;

    @ApiModelProperty(value = "文件id", notes = "用于预览")
    private Long fileId;

    @ApiModelProperty("模板文件名称")
    private String filename;

    @ApiModelProperty("模板类型")
    private String templateType;

    @ApiModelProperty("创建人id")
    private Long createBy;

    @ApiModelProperty("创建人名称")
    private String createByName;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 合同面签是否需要展示 0：不需要 1：需要
     */
    @ApiModelProperty(value = "合同面签是否需要展示")
    private Integer faceSignShowFlag;

}

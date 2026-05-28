package cn.zswltech.mithras.dto.archives;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @create: 2023-02-23
 **/

@Data
public class ArchivesSearchREQ {

    @ApiModelProperty("归档id")
    @NotNull
    private Long id;

    @ApiModelProperty("资料类型")
    private String groupName;

    @ApiModelProperty("搜索内容")
    private String content;
}
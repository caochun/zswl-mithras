package cn.zswltech.mithras.dto.archives;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2023-03-13
 **/

@Data
public class ArchiveTemplateInfoREQ {
    @ApiModelProperty("模版id")
    private Long templateId;
}

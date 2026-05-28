package cn.zswltech.mithras.dto.archives;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2023-02-23
 **/

@Data
public class ArchivesDownloadEffectREQ {

    @ApiModelProperty("模版状态")
    private Integer status;

}
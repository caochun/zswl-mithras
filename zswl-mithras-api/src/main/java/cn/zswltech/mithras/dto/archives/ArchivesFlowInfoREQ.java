package cn.zswltech.mithras.dto.archives;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2023-02-23
 **/

@Data
public class ArchivesFlowInfoREQ {

    @ApiModelProperty("批次")
    private String batch;

}
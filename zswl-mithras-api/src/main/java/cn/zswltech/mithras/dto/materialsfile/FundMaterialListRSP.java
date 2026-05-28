package cn.zswltech.mithras.dto.materialsfile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/12/26 10:18
 */
@ApiModel
@Data
public class FundMaterialListRSP {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("文件名")
    private String fileName;
}

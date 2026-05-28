package cn.zswltech.mithras.dto.contract;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @create: 2022-07-21
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class MaterialsListRsp implements Serializable {

    @ApiModelProperty("文件id")
    private Long fileId;

    @ApiModelProperty("文件名称")
    private String fileName;

    @ApiModelProperty("是否被编辑")
    private Integer isEdit;
}

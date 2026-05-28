package cn.zswltech.mithras.dto.afterlease;

import cn.zswltech.mithras.dto.file.FileListRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 租后-罚息减免基本表
 * @author jackerhe
 * @date 2022-11-19
 */
@Data
@ApiModel("租后-逾期催收-返回体")
public class CollectionOverdueRSP {

    /**
    * 原因简述
    */
    @ApiModelProperty(value = "催收层级 RentCollectionLevelEnum")
    private String collectionLevel;

    private List<FileListRSP> fileList;

}

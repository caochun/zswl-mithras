package cn.zswltech.mithras.dto.contract.text;

import cn.zswltech.mithras.dto.file.FileListRSP;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author bigbear
 * @date 2024/12/10 11:39
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ContractTextSignInfoSignPhotosAndVideosRSP extends FileListRSP {

    @ApiModelProperty(value = "上传地点")
    private String location;

}

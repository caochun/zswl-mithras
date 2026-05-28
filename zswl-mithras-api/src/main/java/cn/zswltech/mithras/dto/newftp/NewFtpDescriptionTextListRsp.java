package cn.zswltech.mithras.dto.newftp;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/24 11:46
 */
@Data
@ApiModel("FTP描述文本详情-返回体")
public class NewFtpDescriptionTextListRsp extends ListBaseRSP {
    
    @ApiModelProperty(value = "描述类型")
    private String descType;
    @ApiModelProperty(value = "描述文本内容")
    private String descContent;
}

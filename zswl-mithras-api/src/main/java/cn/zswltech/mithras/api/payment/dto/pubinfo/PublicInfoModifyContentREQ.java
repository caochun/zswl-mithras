package cn.zswltech.mithras.api.payment.dto.pubinfo;

import cn.zswltech.mithras.dto.file.FileListRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author bigbear
 * @date 2024/9/10 17:43
 * @description 修改表格内容请求体
 */
@Data
@ApiModel(value = "公开信息-修改表格内容请求体")
public class PublicInfoModifyContentREQ {

    @ApiModelProperty(value = "当前表格ID")
    @NotNull(message = "当前表格ID不能为空")
    private Long id;

    @ApiModelProperty(value = "表格内容")
    private List<PublicInfoQueryRSP.RowStructure> rowList;
}

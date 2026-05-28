package cn.zswltech.mithras.dto.contract.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/19
 * @description
 */
@Data
@ApiModel("删除合同文本-请求体")
public class ContractFileRemoveREQ {
    @NotNull(message = "文件id不能为空")
    @ApiModelProperty("文件id")
    private Long id;
}

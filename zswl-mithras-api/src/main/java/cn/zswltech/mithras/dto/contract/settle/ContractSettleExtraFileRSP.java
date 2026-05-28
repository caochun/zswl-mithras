package cn.zswltech.mithras.dto.contract.settle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2022/8/24
 * @description
 */
@Data
@ApiModel("合同结清补充协议-返回体")
public class ContractSettleExtraFileRSP {
    @ApiModelProperty("文件id")
    private Long fileId;

    @ApiModelProperty("文件名称")
    private String fileName;
}

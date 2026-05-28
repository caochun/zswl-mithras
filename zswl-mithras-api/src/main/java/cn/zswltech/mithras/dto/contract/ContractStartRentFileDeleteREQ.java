package cn.zswltech.mithras.dto.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author wangchuanhao
 * @date 2022/8/23 11:02 AM
 */
@Data
@ApiModel("合同管理-合同起租-附件删除-基础请求体")
public class ContractStartRentFileDeleteREQ {

    @ApiModelProperty(value = "合同id")
    @NotNull
    private Long contractId;

    @ApiModelProperty("文件id")
    @NotNull
    private Long fileId;

}

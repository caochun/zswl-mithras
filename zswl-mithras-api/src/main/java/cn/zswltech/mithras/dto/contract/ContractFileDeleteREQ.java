package cn.zswltech.mithras.dto.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 *
 * @author wangchuanhao
 * @date 2022/8/23 11:02 AM
 */
@Data
@ApiModel("合同管理-合同变更删除补充协议-基础请求体")
public class ContractFileDeleteREQ {

    @ApiModelProperty("变更类型;CHANGE_INTEREST（调息）、EARLY_REPAYMENT（提前还款）、EXTENSION（展期）、OTHER（其他）")
    @NotBlank
    private String changeType;

    @ApiModelProperty(value = "合同id")
    @NotNull
    private Long contractId;

    @ApiModelProperty("文件id")
    @NotNull
    private Long fileId;

}

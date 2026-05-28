package cn.zswltech.mithras.dto.contract;

import cn.zswltech.mithras.dto.process.modify.remark.ProcessModifyRemarkAddREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * @author wangchuanhao
 * @date 2022/8/23 11:02 AM
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("合同管理-合同变更-列表-基础请求体")
public class ContractFlowChangeREQ extends ContractFlowBasicREQ {

    @ApiModelProperty("变更类型;CHANGE_INTEREST（调息）、EARLY_REPAYMENT（提前还款）、EXTENSION（展期）、OTHER（其他）")
    @NotBlank
    private String changeType;

    @ApiModelProperty("实际起租日（yyyy-MM-dd）")
    private String actualLeaseDate;

    @ApiModelProperty("只是检验是否能提交审批")
    private Boolean onlyCheck = false;

    @ApiModelProperty("变更说明")
    private ProcessModifyRemarkAddREQ remarkAddREQ;

}

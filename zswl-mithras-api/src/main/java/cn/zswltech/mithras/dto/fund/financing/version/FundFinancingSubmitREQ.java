package cn.zswltech.mithras.dto.fund.financing.version;

import cn.zswltech.mithras.dto.process.modify.remark.ProcessModifyRemarkAddREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/2/20
 * @description
 */
@Data
@ApiModel("融资管理-提交审批-请求体")
public class FundFinancingSubmitREQ {
    @NotNull
    @ApiModelProperty("融资Id")
    public Long id;

    @NotNull
    @ApiModelProperty("只是检验是否能提交审批")
    private Boolean onlyCheck = false;

    @ApiModelProperty("变更说明")
    private ProcessModifyRemarkAddREQ remarkAddREQ;
}

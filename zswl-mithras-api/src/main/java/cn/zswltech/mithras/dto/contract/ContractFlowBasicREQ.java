package cn.zswltech.mithras.dto.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/22
 * @description
 */
@Data
@ApiModel("合同管理-合同相关提交审批-基础请求体")
public class ContractFlowBasicREQ {
    @NotNull(message = "主合同id不能为空")
    @ApiModelProperty("主合同id")
    private Long contractId;


    @ApiModelProperty("备注")
    @Length(max = 2500, message = "备注长度不能超过2500")
    private String remark;

    @ApiModelProperty("只是检验是否能提交审批")
    private Boolean onlyCheck = false;
}

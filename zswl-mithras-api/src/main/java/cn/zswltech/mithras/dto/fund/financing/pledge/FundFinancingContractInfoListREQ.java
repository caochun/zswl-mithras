package cn.zswltech.mithras.dto.fund.financing.pledge;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author bigbear
 * @date 2025/4/14 15:16
 * @description
 */
@Data
@ApiModel(value = "融资管理-合同信息-请求体")
public class FundFinancingContractInfoListREQ {

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "去除锁定合同标记")
    private String removeLockFlag;
}

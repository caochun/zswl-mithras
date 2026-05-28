package cn.zswltech.mithras.dto.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 *
 *
 * @author
 * @date 2022/8/23 11:02 AM
 */
@Data
@ApiModel("合同管理-合同变更判断当前合同流程-基础请求体")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractCanChangeRSP  {

    @ApiModelProperty("是否可继续流程")
    private Boolean canProcess;

    @ApiModelProperty("当前流程")
    private String status;

    @ApiModelProperty("二级流程")
    private String twoStatus;

    @ApiModelProperty("阻断提示")
    private String message;

}

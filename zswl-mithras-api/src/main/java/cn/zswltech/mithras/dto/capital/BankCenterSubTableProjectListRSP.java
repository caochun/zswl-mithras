package cn.zswltech.mithras.dto.capital;

import cn.zswltech.mithras.dto.capital.base.BusinessFlowBaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @author yangxiong
 * @date 2024/5/18/14:49
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BankCenterSubTableProjectListRSP extends BusinessFlowBaseModel {

    @ApiModelProperty(value = "业务ID")
    private Long id;

    @ApiModelProperty("流水类型 cqBillTypeEnum")
    private String platform;

    @ApiModelProperty("来源途径 ExceptionSourceENUM")
    private String source;

    @ApiModelProperty(value = "客户ID")
    private Long clientId;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "现金流编号")
    private String cashFlowCode;

    @ApiModelProperty(value = "付款实际核销ID")
    private Long paymentActualDetailId;
}

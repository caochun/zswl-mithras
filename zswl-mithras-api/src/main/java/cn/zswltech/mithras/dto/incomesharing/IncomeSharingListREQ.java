package cn.zswltech.mithras.dto.incomesharing;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author yupengfei
 * @date 2024/6/7 15:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class IncomeSharingListREQ extends PageReq {

    @NotBlank(message = "月份不得为空")
    @ApiModelProperty(value = "查询月份 yyyy-MM")
    private String yearAndMonth;

    @ApiModelProperty(value = "客户名称")
    private Long clientId;

    @ApiModelProperty(value = "项目名称")
    private String projName;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "借据编号")
    private String receiptCode;

    @ApiModelProperty(value = "合同状态")
    private String contractStatus;

    @ApiModelProperty(value = "是否逾期:OVERDUE(逾期)，NOT_OVERDUE(未逾期)")
    private String overdueType;

}

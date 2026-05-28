package cn.zswltech.mithras.dto.monthly;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("实际利率法-列表-参数")
public class MonthlyAIRListREQ extends PageReq {

    @NotBlank(message = "月份不得为空")
    @ApiModelProperty(value = "处理月份 yyyy-MM")
    private String yearAndMonth;

    @ApiModelProperty(value = "客户ID")
    private Long clientId;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    /**
     * @link OverdueTypeEnum
     */
    @ApiModelProperty(value = "是否逾期 OverdueTypeEnum")
    private String overdueType;

    private Integer isConfirmed;

    private String batchNumber;
}

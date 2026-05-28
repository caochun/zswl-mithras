package cn.zswltech.mithras.dto.fund.receiptrepay.version;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

/**
 * 批量审批生成批次-入参
 *
 * @author wangchuanhao
 * @date 2023/2/18 5:07 PM
 */
@Data
@ApiModel("批量审批生成批次-入参")
public class CreateBatchREQ {

    @ApiModelProperty("类型：批量：BATCH,自动：AUTO")
    @NotBlank
    private String batchType;

    @ApiModelProperty("批量付款id列表")
    private List<Long> receiptIdList;

    @ApiModelProperty("还款月份")
    private LocalDate repayMonth;

}

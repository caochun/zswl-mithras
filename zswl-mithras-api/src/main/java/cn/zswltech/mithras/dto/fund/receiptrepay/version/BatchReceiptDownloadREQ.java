package cn.zswltech.mithras.dto.fund.receiptrepay.version;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量提交审批-入参
 *
 * @author wangchuanhao
 * @date 2023/2/18 5:07 PM
 */
@Data
@ApiModel("批量提交审批-入参")
public class BatchReceiptDownloadREQ {

    @ApiModelProperty("批次id")
    @NotNull
    private Long batchId;

    @ApiModelProperty("下载的付款id列表")
    @NotEmpty
    private List<Long> receiptIdList;

}

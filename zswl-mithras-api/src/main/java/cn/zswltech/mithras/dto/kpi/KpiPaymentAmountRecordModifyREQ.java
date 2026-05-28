package cn.zswltech.mithras.dto.kpi;
import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 绩效考核-投放信息记录表
 * @author vico
 * @date 2024-09-27
 */
@Data
@ApiModel("绩效考核-投放信息记录表编辑-请求体")
public class KpiPaymentAmountRecordModifyREQ {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
    * 生效月份
    */
    @ApiModelProperty(value = "生效月份")
    private LocalDateTime effectMonth;

    /**
    * 批次号
    */
    @ApiModelProperty(value = "批次号")
    private Integer batchNumber;

    /**
    * 合同id
    */
    @ApiModelProperty(value = "合同id")
    private Long contractId;

    /**
    * 当月投放金额
    */
    @ApiModelProperty(value = "当月投放金额")
    private Long paymentAmount;

}

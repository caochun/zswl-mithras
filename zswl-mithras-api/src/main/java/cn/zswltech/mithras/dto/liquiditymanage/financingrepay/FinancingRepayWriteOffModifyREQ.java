package cn.zswltech.mithras.dto.liquiditymanage.financingrepay;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * AccountBalanceListREQ
 *
 * @author zhouning
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "还款核销确认参数")
public class FinancingRepayWriteOffModifyREQ {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 融资id
     */
    @ApiModelProperty(value = "融资id")
    private Long financingId;

    /**
     * 融资类型：直融/间融
     */
    @TableField("financing_type")
    private String financingType;


    /**
     * 实际还款表id
     */
    @ApiModelProperty(value = "实际还款表id")
    private Long financingRepayActualId;


    @ApiModelProperty(value = "核销状态是否已确认")
    private Integer isPaid;

}

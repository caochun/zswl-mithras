package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/19/13:31
 * @description
 */
@Data
public class BankFlowProcessingCenterProjWriteOffREQ {

    /**
     * 流水列表ID
     */
    @ApiModelProperty(value = "流水ID列表")
    @NotEmpty(message = "流水ID列表不能为空")
    private List<Long> financeFlowIds;

    /**
     * 类型：项目端或者资金端
     */
    @ApiModelProperty(value = "类型：项目端或者资金端")
    private String sideType;

    /**
     * 类型：付款或者收款
     */
    @ApiModelProperty(value = "类型：付款或者收款")
    private String writeOffType;

    /**
     * 数据列表json 根据不同的核销类型找付款或者收款
     */
    @ApiModelProperty(value = "数据列表json")
    private String listDataJson;

}

package cn.zswltech.mithras.dto.finance.overdue;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description 应收逾期集成表
 * @author vico
 * @date 2025-09-15
 */
@Data
@ApiModel("应收逾期集成表推送-请求体")
public class FinanceOverdueIntegrationPushRSP {

    /**
    * 收款明细id
    */
    @ApiModelProperty(value = "总数量")
    private int count;

    @ApiModelProperty(value = "成功条数")
    private int success;

    @ApiModelProperty(value = "失败条数")
    private int failure;;

    @ApiModelProperty(value = "失败信息")
    private List<String> message;


}

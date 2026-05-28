package cn.zswltech.mithras.dto.fund.directfinancing;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ylzhang5
 * @description 直接融资/间接融资-投放资产明细
 * @date 20251210
 */
@Data
@ApiModel("直接融资/间接融资-投放资产明细列表-请求体")
public class FundFinancingPropertyListREQ extends PageReq {

    /**
     * 融资id
     */
    @ApiModelProperty("融资id")
    private Long financingId;
    

}

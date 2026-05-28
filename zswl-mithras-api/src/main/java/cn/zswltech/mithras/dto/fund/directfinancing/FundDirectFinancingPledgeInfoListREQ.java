package cn.zswltech.mithras.dto.fund.directfinancing;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 直接融资-质押明细
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-质押明细列表-请求体")
public class FundDirectFinancingPledgeInfoListREQ extends PageReq {

    /**
     * 融资id
     */
    @ApiModelProperty("融资id")
    private Long financingId;
    

}

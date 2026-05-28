package cn.zswltech.mithras.dto.fund.financing.pledge;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: jackerhe
 * @date: 2023/2/20 2:57 下午
 **/
@Data
@ApiModel("融资管理-列表-请求体")
public class FundFinancingPledgeListREQ extends VersionBaseREQ {

    /**
     * 融资id
     */
    @ApiModelProperty("融资id")
    private Long financingId;

}

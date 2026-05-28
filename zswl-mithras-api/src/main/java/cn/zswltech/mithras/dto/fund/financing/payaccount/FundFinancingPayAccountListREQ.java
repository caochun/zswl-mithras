package cn.zswltech.mithras.dto.fund.financing.payaccount;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FundFinancingPayAccountBankREQ
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/2/21 10:04 上午
 * @Version 1.0
 **/
@Data
@ApiModel("融资管理-还款账户查询-请求体")
public class FundFinancingPayAccountListREQ extends VersionBaseREQ {

    /**
     * 融资id
     */
    @ApiModelProperty("融资id")
    private Long financingId;

}

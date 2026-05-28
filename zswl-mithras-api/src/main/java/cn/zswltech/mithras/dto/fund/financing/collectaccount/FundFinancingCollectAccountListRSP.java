package cn.zswltech.mithras.dto.fund.financing.collectaccount;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @author: jackerhe 
 * @date: 2023/2/20 2:57 下午
 **/
@Data
@ApiModel("融资管理-查询对方收款账户列表-返回体")
public class FundFinancingCollectAccountListRSP extends ListBaseRSP {

    @ApiModelProperty("id")
    private Long id;

    /**
     * 融资id
     */
    @ApiModelProperty("融资id")
    private Long financingId;

    /**
     * 客户名称
     */
    @ApiModelProperty("客户名称")
    private String clientName;
    /**
     * 账户名称
     */
    @ApiModelProperty("账户名称")
    private String accountName;
    /**
     * 银行账号
     */
    @ApiModelProperty("银行账号")
    private String accountNum;
    /**
     * 开户行
     */
    @ApiModelProperty("开户行")
    private String accountAddress;

}

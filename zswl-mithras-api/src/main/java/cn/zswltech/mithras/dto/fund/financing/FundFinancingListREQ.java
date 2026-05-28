package cn.zswltech.mithras.dto.fund.financing;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/20
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("融资管理-首页列表-请求体")
public class FundFinancingListREQ extends PageReq {
    @ApiModelProperty("融资机构id")
    private Long organizationId;

    @ApiModelProperty("融资编号")
    private String financingCode;

    @ApiModelProperty("融资金额（起）")
    private Long financingAmountFrom;

    @ApiModelProperty("融资金额（止）")
    private Long financingAmountTo;

    @ApiModelProperty("融资状态")
    private List<String> financingStatus;

    @ApiModelProperty("资金经理id")
    private Long moneyManagerId;

    @ApiModelProperty("借款日期（起） yyyy-MM-dd")
    private String actualLoanDateFrom;

    @ApiModelProperty("借款日期（止） yyyy-MM-dd")
    private String actualLoanDateTo;

    @ApiModelProperty("创建时间（起） yyyy-MM-dd")
    private String createTimeFrom;

    @ApiModelProperty("创建时间（止） yyyy-MM-dd")
    private String createTimeTo;

    @ApiModelProperty("变更时间（起） yyyy-MM-dd")
    private String updateTimeFrom;

    @ApiModelProperty("变更时间（止） yyyy-MM-dd")
    private String updateTimeTo;

    @ApiModelProperty("是否有质押资产 0-无 1-有")
    private Integer hasPledgeInfo;

    @ApiModelProperty("融资到期日（起） yyyy-MM-dd")
    private String actualExpireDateFrom;

    @ApiModelProperty("融资到期日（止） yyyy-MM-dd")
    private String actualExpireDateTo;

    @ApiModelProperty("业务类型")
    private List<String> businessTypeList;
}

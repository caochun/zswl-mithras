package cn.zswltech.mithras.dto.finance.accountage;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @description 帐龄-详情表
 * @author vico
 * @date 2024-09-10
 */
@Data
@ApiModel("帐龄-详情表列表-请求体")
public class FinanceAccountAgeItemListREQ extends PageReq {

    @ApiModelProperty(value = "帐龄id")
    private Long accountAgeId;

    @ApiModelProperty(value = "苍穹推送状态， 0推送成功 ,1 推送失败")
    private String sendStatus;

    @ApiModelProperty("客商编码")
    private String customerUnitName;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "项目名称")
    private String projName;

    @ApiModelProperty(value = "合同逾期日期")
    private LocalDate planCollectionDateFrom;

    @ApiModelProperty(value = "合同逾期日期")
    private LocalDate planCollectionDateTo;

    private List<Long> ids;

}

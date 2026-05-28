package cn.zswltech.mithras.dto.collection;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @create: 2022-08-15
 **/

@Data
public class CollectionBaseInfoREQ extends PageReq {

    @ApiModelProperty("客户id")
    private String clientId;

    @ApiModelProperty("核销状态")
    private String status;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("现金流项目")
    private String cashFlowItem;

    @ApiModelProperty("计划收款金额-从")
    private Long amountFrom;

    @ApiModelProperty("计划收款金额-到")
    private Long amountTo;

    @ApiModelProperty("实收日期-从")
    private LocalDate collectionDateFrom;

    @ApiModelProperty("实收日期-到")
    private LocalDate collectionDateTo;

    @ApiModelProperty("计划收款日期-从")
    private LocalDate planCollectionDateFrom;

    @ApiModelProperty("计划收款日期-到")
    private LocalDate planCollectionDateTo;

    private List<Long> deptIdList;

    private Boolean isBizUser;

    private Long currentUserId;

}

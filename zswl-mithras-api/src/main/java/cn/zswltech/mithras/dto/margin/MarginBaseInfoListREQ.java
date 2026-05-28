package cn.zswltech.mithras.dto.margin;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @create: 2022-08-15
 **/

@Data
@ApiModel("保证金列表-请求体")
public class MarginBaseInfoListREQ extends PageReq {

    @ApiModelProperty("客户id")
    private String clientId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("保证金金额-从")
    private Long amountFrom;

    @ApiModelProperty("保证金金额-到")
    private Long amountTo;

    @ApiModelProperty("实收日期-从")
    private LocalDate collectionDateFrom;

    @ApiModelProperty("实收日期-到")
    private LocalDate collectionDateTo;

    private List<Long> deptIdList;

    private Boolean isBizUser;

    private Long currentUserId;
}

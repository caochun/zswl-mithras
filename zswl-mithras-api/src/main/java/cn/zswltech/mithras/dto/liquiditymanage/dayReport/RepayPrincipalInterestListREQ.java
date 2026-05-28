package cn.zswltech.mithras.dto.liquiditymanage.dayReport;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author bigbear
 * @date 2024/12/13 16:44
 * @className RepayPrincipalInterestListREQ
 * @description
 */
@Data
@ApiModel(value = "还款本金利息列表请求参数")
@EqualsAndHashCode(callSuper = true)
public class RepayPrincipalInterestListREQ extends PageReq {

    @ApiModelProperty(value = "查询日期-开始")
    private LocalDate queryDateFrom;

    @ApiModelProperty(value = "查询日期-结束")
    private LocalDate queryDateTo;
}

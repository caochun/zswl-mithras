package cn.zswltech.mithras.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2023/6/25
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("小工具-会计利润测算-分页列表-请求参数")
public class ProfitCalculateResultListREQ extends PageReq {
    @ApiModelProperty("合同编号")
    private String contractCode;
}

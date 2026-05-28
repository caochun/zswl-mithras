package cn.zswltech.mithras.dto.fund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/12/14 11:00
 */
@Data
@ApiModel("担保机构同步天眼查数据请求体")
public class FundGuaranteeAgencySyncREQ {
    @ApiModelProperty("担保机构id")
    private Long id;
}

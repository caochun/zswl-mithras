package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/11/20 13:23
 */
@ApiModel("list页统计信息-返回体")
@Data
public class AfterLeaseCheckExternalQueryListStatisticsRsp {
    @ApiModelProperty("当月在租项目项目")
    private Integer totalCount;

    @ApiModelProperty("完成报告项目数")
    private Integer doneCount;
}

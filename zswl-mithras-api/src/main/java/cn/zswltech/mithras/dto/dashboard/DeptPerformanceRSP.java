package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author yangxiong
 * @date 2024/6/24/15:29
 * @description
 */
@Data
@ApiModel(value = "部门业绩响应体")
public class DeptPerformanceRSP {

    @ApiModelProperty(value = "数据更新时间 格式：yyyy-MM-dd")
    private String dataUpdateTime;

    @ApiModelProperty(value = "卡片列表")
    private List<CardListVo> cardList;

    @ApiModelProperty(value = "数据列表")
    private List<DeptShipSortPerformanceRSP> dataList;
}

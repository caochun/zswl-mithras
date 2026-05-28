package cn.zswltech.mithras.dto.managereport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/12/16
 * @description
 */
@Data
public class YunYingDaiBanStatisticRSP {
    @ApiModelProperty("流程类型展示")
    private String processDisplay;

    @ApiModelProperty("已到达-运营经办-流程ID集合")
    private List<String> arriveYYJBProcessInstanceIds;

    @ApiModelProperty("已到达-运营复核-流程ID集合")
    private List<String> arriveYYFHProcessInstanceIds;

    @ApiModelProperty("已到达-运营负责人-流程ID集合")
    private List<String> arriveYYFZRProcessInstanceIds;

    @ApiModelProperty("已到达-合计-流程ID集合")
    private List<String> arriveHJProcessInstanceIds;

    @ApiModelProperty("将到达-运营经办-流程ID集合")
    private List<String> willArriveYYJBProcessInstanceIds;

    @ApiModelProperty("将到达-运营复核-流程ID集合")
    private List<String> willArriveYYFHProcessInstanceIds;

    @ApiModelProperty("将到达-运营负责人-流程ID集合")
    private List<String> willArriveYYFZRProcessInstanceIds;

    @ApiModelProperty("将达到-合计-流程ID集合")
    private List<String> willArriveHJProcessInstanceIds;

    public YunYingDaiBanStatisticRSP() {
        arriveYYJBProcessInstanceIds = new LinkedList<>();
        arriveYYFHProcessInstanceIds = new LinkedList<>();
        arriveYYFZRProcessInstanceIds = new LinkedList<>();
        arriveHJProcessInstanceIds = new LinkedList<>();
        willArriveYYJBProcessInstanceIds = new LinkedList<>();
        willArriveYYFHProcessInstanceIds = new LinkedList<>();
        willArriveYYFZRProcessInstanceIds = new LinkedList<>();
        willArriveHJProcessInstanceIds = new LinkedList<>();
    }
}

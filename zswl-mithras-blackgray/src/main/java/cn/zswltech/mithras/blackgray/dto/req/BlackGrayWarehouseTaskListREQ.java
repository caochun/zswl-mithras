package cn.zswltech.mithras.blackgray.dto.req;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description 黑灰名单任务表
 * @author 
 * @date 2024-01-16
 */
@Data
@ApiModel("黑灰名单任务表列表-请求体")
public class BlackGrayWarehouseTaskListREQ extends PageReq {

    /**
     * 来源
     **/
    @ApiModelProperty(name = "业务来源 BlackGraySourceEnum")
    private String businessSource;

    @ApiModelProperty(value = "任务编号")
    private String taskNum;

    @ApiModelProperty(value = "数据时点，即用户提交任务时所在的月份")
    private String timePoint;

    @ApiModelProperty(value = "机构code")
    private String orgCode;

    @ApiModelProperty(value = "审批状态 0=wait， 1=ing， 2=withdraw， 3=reject， 4=finish")
    private List<Integer> auditStatus;

    @ApiModelProperty(value = "定期任务是否超时，0 不超时，1超时")
    private Integer overtimeFlag;

}

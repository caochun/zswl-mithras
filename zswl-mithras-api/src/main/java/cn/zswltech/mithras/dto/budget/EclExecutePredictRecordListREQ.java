package cn.zswltech.mithras.dto.budget;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @description 资产减值预测详情记录表
 * @author vico
 * @date 2025-10-14
 */
@Data
@ApiModel("资产减值预测详情记录表列表-请求体")
public class EclExecutePredictRecordListREQ extends PageReq {

    /**
     * 预测计划id
     */
    @ApiModelProperty("execute_predict_id")
    @NotNull(message = "预算id不能为空")
    private Long executePredictId;

    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称")
    private String clientName;

    /**
     * 合同编号
     */
    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "测算日期")
    private LocalDate updateTimeFrom;

    @ApiModelProperty(value = "测算日期")
    private LocalDate updateTimeTo;

    @ApiModelProperty(value = "是否最新数据")
    private boolean fastFlag = false;

    @ApiModelProperty(value = "内评级别")
    private String innerMdLevel;

    @ApiModelProperty(value = "所属分组")
    private String group;

    @ApiModelProperty(value = "五级分类")
    private String classify;

    @ApiModelProperty(value = "债项阶段")
    private String eclStep;

    @ApiModelProperty(value = "租赁物类型")
    private String leaseType;

}

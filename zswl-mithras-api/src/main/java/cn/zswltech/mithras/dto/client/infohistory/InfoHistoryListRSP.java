package cn.zswltech.mithras.dto.client.infohistory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author luyi
 */
@Data
@ApiModel("变更历史列表-返回体")
public class InfoHistoryListRSP {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("变更编号")
    private String historyCode;

    @ApiModelProperty("模块")
    private String moduleCode;

    @ApiModelProperty("记录id")
    private Long moduleRecordId;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("操作类型")
    private String operationType;

  /*  @ApiModelProperty("变更前数据")
    private String originalData;

    @ApiModelProperty("变更后数据")
    private String currentData;*/

}

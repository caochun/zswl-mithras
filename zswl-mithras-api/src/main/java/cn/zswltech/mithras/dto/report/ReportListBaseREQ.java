package cn.zswltech.mithras.dto.report;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 征信报送列表查询
 *
 * @author wangchuanhao
 * @date 2023/1/11 1:55 PM
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportListBaseREQ extends PageReq {

    @ApiModelProperty("查询渠道：EDIT编辑区；PROC审批流里；PROC_BATCH批次查询；EFFECT已报送账户维度穿透")
    @NotBlank
    private String channel;

    @ApiModelProperty("审批状态")
    private String approvalStatus;

    @ApiModelProperty("流程中查询时（channel=2）必填，审批流businessKey")
    private String procBusinessKey;

    @ApiModelProperty("批次查询时（channel=3）必填，批次id")
    private Long batchId;

    @ApiModelProperty("已报送账户维度穿透查询时（channel=4）必填，账号表id")
    private Long accountId;

}

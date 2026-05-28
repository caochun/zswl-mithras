package cn.zswltech.mithras.dto.afterlease;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2022/11/17
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("租后检查报告-非公用事业补充说明-返回体")
public class AfterLeaseCheckReportNonPublicExtraRSP extends ListBaseRSP {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("模板id")
    private Long templateId;

    @ApiModelProperty("模板code")
    private String templateCode;

    @ApiModelProperty("模板条目")
    private String templateTitle;

    @ApiModelProperty("检查结果")
    private Integer checkResult;

    @ApiModelProperty("备注")
    private String remark;
}

package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/17
 * @description
 */
@Data
@ApiModel("租后检查报告-非公用事业补充说明-请求体")
public class AfterLeaseCheckReportNonPublicExtraREQ {
    @ApiModelProperty("检查计划客户记录id")
    @NotNull(message = "检查计划客户记录id不能为空")
    private Long checkPlanClientId;

    @Valid
    @ApiModelProperty("补充说明内容列表")
    @NotEmpty(message = "补充说明内容列表不能为空")
    private List<Data> contentList;

    @lombok.Data
    public static class Data {
        @ApiModelProperty("id")
        private Long id;

        @NotNull(message = "模板id不能为空")
        @ApiModelProperty("模板id")
        private Long templateId;

        @NotNull(message = "检查结果不能为空")
        @ApiModelProperty("检查结果")
        private Integer checkResult;

        @ApiModelProperty("备注")
        private String remark;
    }
}

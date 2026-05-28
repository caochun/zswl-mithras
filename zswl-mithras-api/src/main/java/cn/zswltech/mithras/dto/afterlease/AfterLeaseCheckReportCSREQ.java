package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/17
 * @description
 */
@Data
@ApiModel("租后检查报告-检查内容/总结-请求体")
public class AfterLeaseCheckReportCSREQ {
//    @ApiModelProperty("检查计划客户数据id")
//    @NotNull(message = "检查计划客户数据id不能为空")
//    private Long checkPlanClientId;
//
//    @Valid
//    @ApiModelProperty("检查内容/总结列表")
//    @NotEmpty(message = "检查内容/总结列表不能为空")
//    private List<Content> contentList;
//
//    @Data
//    public static class Content {
//        @ApiModelProperty("检查内容/总结id")
//        private Long id;
//
//        @NotNull(message = "检查内容/总结模板id不能为空")
//        @ApiModelProperty("检查内容/总结模板id")
//        private Long templateId;
//
//        @ApiModelProperty("填写/选择的检查内容/总结")
//        private String content;
//    }

    @ApiModelProperty("检查计划客户数据id")
    @NotNull(message = "检查计划客户数据id不能为空")
    private Long checkPlanClientId;

    @Valid
    @ApiModelProperty("检查内容/总结")
    @NotEmpty(message = "检查内容/总结不能为空")
    private List<Content> contentList;

    /**
     * 暂存：DRAFT   保存：SAVE
     */
    @ApiModelProperty(value = "保存类型")
    @NotBlank(message = "保存类型不能为空")
    private String saveType;

    @Data
    public static class Content {
        private Integer moduleIndex;
        private String fieldName;
        private String fieldValue;
        private String attributionList;
        private String situationExplain;
    }
}

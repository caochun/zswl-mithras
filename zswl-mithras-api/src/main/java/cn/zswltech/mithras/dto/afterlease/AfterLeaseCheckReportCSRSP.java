package cn.zswltech.mithras.dto.afterlease;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.dto.OptionDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("租后检查报告-检查内容/总结-返回体")
public class AfterLeaseCheckReportCSRSP extends ListBaseRSP {
//    @ApiModelProperty("内容/总结分组名称")
//    private String groupName;
//
//    @ApiModelProperty("内容/总结列表")
//    private List<Content> contentList;
//
//    @EqualsAndHashCode(callSuper = true)
//    @Data
//    public static class Content extends ListBaseRSP {
//        @ApiModelProperty("内容/总结模板条目id")
//        private Long templateId;
//
//        @ApiModelProperty("内容/总结模板条目code")
//        private String templateCode;
//
//        @ApiModelProperty("内容/总结条目名称")
//        private String templateTitle;
//
//        @ApiModelProperty("内容/总结输入标签")
//        private String templateContentInputLabel;
//
//        @ApiModelProperty("内容/总结输入类型")
//        private String templateContentInputType;
//
//        @ApiModelProperty("输入类型枚举（下拉框、单选框等）")
//        private List<OptionRSP> templateOptionList;
//
//        @ApiModelProperty("内容/总结id")
//        private Long id;
//
//        @ApiModelProperty("内容")
//        private String content;
//    }
    @ApiModelProperty("检查计划客户数据id")
    @NotNull(message = "检查计划客户数据id不能为空")
    private Long checkPlanClientId;

    @Valid
    @ApiModelProperty("检查内容/总结")
    @NotEmpty(message = "检查内容/总结不能为空")
    private List<AfterLeaseCheckReportCSRSP.Content> contentList;

    @Data
    public static class Content {
        private Integer moduleIndex;
        private String fieldName;
        private String fieldRemark;
        private String fieldValue;
        private String attributionList;
//        private String fieldType;
//        private List<OptionDTO> fieldOption;
    }
}

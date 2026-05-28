package cn.zswltech.mithras.dto.assetclassify;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 
 * @author: jackerhe 
 * @date: 2023/1/8 1:46 下午
 **/
@Data
@ApiModel("风控管理-资产分类-检查内容/总结-返回体")
public class AssetClassifyCheckContentRSP extends ListBaseRSP {
    @ApiModelProperty("内容/总结分组名称")
    private String groupName;

    @ApiModelProperty("内容/总结列表")
    private List<Content> contentList;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Content extends ListBaseRSP {
        @ApiModelProperty("内容/总结模板条目id")
        private Long templateId;

        @ApiModelProperty("内容/总结条目名称")
        private String templateTitle;

        @ApiModelProperty("内容/总结输入类型")
        private String templateContentInputType;

        @ApiModelProperty("内容/总结id")
        private Long id;

        @ApiModelProperty("内容")
        private String content;
    }
}

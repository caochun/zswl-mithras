package cn.zswltech.mithras.dto.assetclassify;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 *
 * @author: jackerhe
 * @date: 2023/1/8 3:18 下午
 **/
@Data
@ApiModel("风控管理-资产分类内容/总结-请求体")
public class AssetClassifyCheckContentModifyREQ {

    @ApiModelProperty("详情id")
    @NotNull(message = "详情id不能为空")
    private Long  assetClassifyClientId;

    @Valid
    @ApiModelProperty("检查内容/总结列表")
    @NotEmpty(message = "检查内容/总结列表不能为空")
    private List<Content> contentList;

    @Data
    public static class Content {
        @ApiModelProperty("检查内容/总结id")
        private Long id;

        @ApiModelProperty("填写/选择的检查内容/总结")
        private String content;
    }
}

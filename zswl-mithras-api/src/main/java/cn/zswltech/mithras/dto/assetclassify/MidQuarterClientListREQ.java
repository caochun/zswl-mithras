package cn.zswltech.mithras.dto.assetclassify;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("风控管理-资产分类-季中初分-请求体")
public class MidQuarterClientListREQ extends PageReq {

    @ApiModelProperty("客户id集合")
    private List<Long> clientIds;

    @ApiModelProperty("年份")
    @NotNull(message = "年份不能为空")
    private Integer year;

    @ApiModelProperty("季度")
    @NotNull(message = "季度不能为空")
    private Integer quarter;

    /**
     * 初分类型：季末初分:QUARTER_END / 季中初分:QUARTER_MID
     */
    private String initType;

    /**
     * 搜索关键词（客户名称）
     */
    @ApiModelProperty("搜索关键词")
    private String keyword;
}

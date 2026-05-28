package cn.zswltech.mithras.dto.assetclassify;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName QuarterDetailREQ
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/1/4 1:59 下午
 * @Version 1.0
 **/
@Data
@ApiModel("五级分类-季度选择返回体")
public class QuarterDetailRSP {

    @ApiModelProperty("季度选择id")
    private Long id;

    @ApiModelProperty("季度")
    private Integer quarter;

    @ApiModelProperty("季度分类信息")
    private List<ClassificationAmount> classificationAmounts;

    @Data
    public class ClassificationAmount{
        /**
         * 分类结果 AssetClassifyResultEnum#name()
         */
        @ApiModelProperty("分类")
        private String classifyResult;

        @ApiModelProperty("排序")
        private Integer sort;

        /**
         *分类数量
         **/
        @ApiModelProperty("分类数量")
        private Integer classifyAmount;
    }

    /**
     * 初分类型：季末初分:QUARTER_END / 季中初分:QUARTER_MID
     */
    @ApiModelProperty("初分类型")
    private String initType;

    /**
     * 季中初分状态：FINISH-完成 / PROCESS 进行中
     */
    @ApiModelProperty("季中初分状态")
    private String midInitStatue;
}

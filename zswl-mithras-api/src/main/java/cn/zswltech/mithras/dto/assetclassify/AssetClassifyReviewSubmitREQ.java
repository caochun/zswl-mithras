package cn.zswltech.mithras.dto.assetclassify;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName AssetclassifyReviewSubmitREQ
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/1/5 3:34 下午
 * @Version 1.0
 **/
@Data
public class AssetClassifyReviewSubmitREQ {

    @NotNull(message = "id不能为空")
    @ApiModelProperty("id")
    private Long id;

}

package cn.zswltech.mithras.dto.projreview.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/3 13:50
 */
@Data
@ApiModel("项目评审关闭-请求体")
public class ProjReviewBaseInfoRemoveREQ {
    @NotEmpty
    @ApiModelProperty("ids")
    private List<Long> ids;
}

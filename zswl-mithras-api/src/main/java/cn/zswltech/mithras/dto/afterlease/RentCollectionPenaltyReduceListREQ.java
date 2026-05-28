package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 租金催收首页列表请求体
 *
 * @author wangchuanhao
 * @date 2022/11/17 3:09 PM
 */
@ApiModel("租金催收首页列表请求体")
@Data
public class RentCollectionPenaltyReduceListREQ {

    @ApiModelProperty("收款id")
    private List<Long> collectionId;

    @ApiModelProperty("罚息减免ID")
    private Long reduceBaseId;

}

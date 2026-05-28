package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description 罚息减免明细表
 * @author vico
 * @date 2024-08-21
 */
@Data
@ApiModel("罚息减免明细表编辑-请求体")
public class PenaltyReduceDetailModifyREQ {

    @ApiModelProperty("id")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty("备注")
    private String notes;

    private List<RentCollectionPenaltyReduceDetailRSP.RentCollectionPenaltyReduceItem> items;

}

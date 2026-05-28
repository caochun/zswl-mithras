package cn.zswltech.mithras.dto.afterlease;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 租后-罚息减免基本表
 * @author jackerhe
 * @date 2022-11-19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("租后-罚息减免基本表列表-请求体")
public class CollectionPenaltyReductionInfoListREQ extends PageReq {

    @ApiModelProperty("罚息减免id")
    private Long id;

    @NotNull(message = "合同ID不能为空")
    @ApiModelProperty("合同ID")
    private Long contractId;

    /**
     * 罚息减免状态
     */
    @ApiModelProperty(value = "罚息减免状态 RecordStatus")
    private List<String> collectionStatus;

}

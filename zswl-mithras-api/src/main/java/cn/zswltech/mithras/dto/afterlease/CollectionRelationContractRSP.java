package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * 租后-罚息减免基本表
 * @author jackerhe
 * @date 2022-11-19
 */
@Data
@ApiModel("租后-罚息减免获取合同ID-返回体")
@Builder
public class CollectionRelationContractRSP {

    /**
    * 合同id
    */
    @ApiModelProperty(value = "合同id")
    private Long contractId;

}

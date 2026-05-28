package cn.zswltech.mithras.dto.afterlease;
import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @description 租后调整信息表
 * @author vico
 * @date 2022-11-08
 */
@Data
@ApiModel("租后调整信息表编辑-请求体")
public class AfterLeaseAdjustInfoModifyREQ {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 展期月数
    */
    @ApiModelProperty(value = "展期月数")
    private Long extensionmonth;

    /**
     * 调整说明
     */
    @ApiModelProperty(value = "调整说明")
    private String adjustExplain;

    /**
    * 风控经理id
    */
    @ApiModelProperty(value = "风控经理id")
    private Long riskControlManagerId;

    /**
    * 法务经理id
    */
    @ApiModelProperty(value = "法务经理id")
    private Long legalManagerUserId;


}

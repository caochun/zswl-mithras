package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description 黑灰名单库-入库原因参数配置
 * @author 
 * @date 2024-01-18
 */
@Data
@ApiModel("黑灰名单库-入库原因参数配置新增-请求体")
public class BlackGrayWarehouseRuleConfigAddREQ {


    /**
    * 规则名称
    */
    @ApiModelProperty(value = "规则名称")
    @NotNull
    private String ruleName;

    /**
    * 父id
    */
    @ApiModelProperty(value = "父id")
    private Long parentId;
    /**
    * 所属金控类型主id，即一级id
    */
    @ApiModelProperty(value = "所属金控类型主id，即一级id")
    private Long mainId;

    /**
    * 黑灰标识
    */
    @ApiModelProperty(value = "黑灰标识")
    @NotNull
    private String blackGrayType;
    /**
    * 业务类型
    */
    @ApiModelProperty(value = "来源 BlackGraySourceEnum EXTERNAL_APPROVAL 外部，INTERNAL_APPROVAL 内部")
    @NotNull
    private String source;

    @ApiModelProperty(value = "适用业务类型")
    @NotNull
    private List<String> suitBusiness;
    /**
    * 适用机构
    */
    @ApiModelProperty(value = "适用机构")
    @NotNull
    private List<String> suitOrg;

}

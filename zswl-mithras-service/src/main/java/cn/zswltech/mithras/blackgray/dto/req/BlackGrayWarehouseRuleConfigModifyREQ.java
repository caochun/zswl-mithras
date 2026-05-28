package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description 黑灰名单库-入库原因参数配置
 * @author 
 * @date 2024-01-18
 */
@Data
@ApiModel("黑灰名单库-入库原因参数配置编辑-请求体")
public class BlackGrayWarehouseRuleConfigModifyREQ {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;
    /**
    * 规则名称
    */
    @ApiModelProperty(value = "规则名称")
    private String ruleName;
    /**
    * 层级 0金控定义
    */
    @ApiModelProperty(value = "层级 0金控定义")
    private Integer level;

    /**
    * 所属金控类型主id，即一级id
    */
    @ApiModelProperty(value = "所属金控类型主id，即一级id")
    private Long mainId;
    /**
    * 状态 0启用，1禁用
    */
    @ApiModelProperty(value = "状态 1启用，0禁用")
    private Integer status;
    /**
    * 黑灰标识
    */
    @ApiModelProperty(value = "黑灰标识")
    private String blackGrayType;

    @ApiModelProperty(value = "来源 BlackGraySourceEnum EXTERNAL_APPROVAL 外部，INTERNAL_APPROVAL 内部")
    private String source;

    @ApiModelProperty(value = "适用业务类型")
    private List<String> suitBusiness;
    /**
    * 适用机构
    */
    @ApiModelProperty(value = "适用机构")
    private List<String> suitOrg;

}

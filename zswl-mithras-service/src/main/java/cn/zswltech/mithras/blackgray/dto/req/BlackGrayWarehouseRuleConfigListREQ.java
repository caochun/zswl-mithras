package cn.zswltech.mithras.blackgray.dto.req;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 黑灰名单库-入库原因参数配置
 * @author 
 * @date 2024-01-18
 */
@Data
@ApiModel("黑灰名单库-入库原因参数配置列表-请求体")
public class BlackGrayWarehouseRuleConfigListREQ extends PageReq {


    /**
     * 规则名称
     */
    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    /**
     * 黑灰标识
     */
    @ApiModelProperty(value = "黑灰标识")
    private String blackGrayType;
    /**
     * 业务类型
     */
    @ApiModelProperty(value = "来源 BlackGraySourceEnum EXTERNAL_APPROVAL 外部，INTERNAL_APPROVAL 内部")
    private String source;

    @ApiModelProperty(value = "关联部门")
    private String suitOrg;

    @ApiModelProperty(value = "关联部门")
    private String suitBusiness;

    @ApiModelProperty(value = "状态 1启用，0禁用")
    private Integer status;

}

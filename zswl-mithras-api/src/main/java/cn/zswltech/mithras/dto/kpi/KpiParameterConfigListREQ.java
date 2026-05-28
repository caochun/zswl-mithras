package cn.zswltech.mithras.dto.kpi;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/10
 * @description
 */
@Data
@ApiModel("绩效考核-参数设置列表-请求体")
public class KpiParameterConfigListREQ extends PageReq {
    @ApiModelProperty("参数设置描述")
    private String configDesc;

    /**
     * 1，项目泪润 2，绩效考核
     */
    @ApiModelProperty("分类查询标志")
    private String queryFlag;

    @ApiModelProperty("查询模块  KpiParameterConfigCodeEnum")
    private List<String> queryEnums;
}

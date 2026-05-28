package cn.zswltech.mithras.dto.kpi;
import lombok.Data;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description ecl_业务配置表
 * @author vico
 * @date 2025-09-24
 */
@Data
@ApiModel("ecl_业务配置表列表-请求体")
public class EclBusinessConfigListREQ extends PageReq {

    /**
     * 配置code
     */
    @ApiModelProperty(value = "配置code EclConfigEnum")
    private String configCode;

}

package cn.zswltech.mithras.dto.property;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * @author ylzhang5
 * @description 投放资产
 * @date 20251213
 */
@Data
@ApiModel("投放资产列表-请求体")
@EqualsAndHashCode(callSuper = true)
public class PutPropertyBaseInfoListREQ extends PageReq {
    @ApiModelProperty(value = "融资编号")
    private String financingCode;
    @ApiModelProperty(value = "合同编号")
    private String contractCode;


}

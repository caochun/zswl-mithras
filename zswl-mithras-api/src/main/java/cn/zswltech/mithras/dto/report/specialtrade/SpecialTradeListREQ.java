package cn.zswltech.mithras.dto.report.specialtrade;

import cn.zswltech.mithras.dto.report.AccountListBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 征信报送-特定交易表查询入参
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@Data
@ApiModel("征信报送-特定交易表查询入参")
public class SpecialTradeListREQ extends AccountListBaseREQ {

    @ApiModelProperty("是否报送")
    private Integer reportFlag;

}

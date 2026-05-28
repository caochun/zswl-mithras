package cn.zswltech.mithras.dto.report.fiveclass;

import cn.zswltech.mithras.dto.report.AccountListBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 征信报送-五级分类表查询入参
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@Data
@ApiModel("征信报送-五级分类表查询入参")
public class FiveClassListREQ extends AccountListBaseREQ {

    @ApiModelProperty("五级分类")
    private String fiveClass;

}

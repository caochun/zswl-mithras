package cn.zswltech.mithras.dto.finance.accountage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @description 帐龄主表
 * @author vico
 * @date 2024-09-10
 */
@Data
@ApiModel("帐龄主表编辑-请求体")
public class FinanceAccountAgeBaseInfoCloseREQ {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;

}

package cn.zswltech.mithras.dto.collection;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 票据管理表
 * @author vico
 * @date 2023-06-05
 */
@Data
@ApiModel("票据管理表列表-请求体")
public class BillManagementListREQ extends PageReq {

    /**
     * 票据类型 收款/付款
     */
    @ApiModelProperty(value = "票据类型 收款/付款 枚举-BillTypeEnum")
    @NotNull(message = "票据类型不能为空")
    private String billType;

    @ApiModelProperty(value = "收款/付款明细ID")
    @NotNull(message = "票据收款/付款明细ID不能为空")
    private Long mainId;

}

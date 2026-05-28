package cn.zswltech.mithras.dto.capital;

import lombok.Data;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author vico
 * @description 保融流水表
 * @date 2024-06-17
 */
@Data
@ApiModel("保融流水表列表-请求体")
public class BrFlowRecordListREQ extends PageReq {
    /**
     * 保融流水id
     */
    @ApiModelProperty(value = "保融流水id")
    private String bruid;

    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称")
    private String orgName;

    /**
     * 账户编码
     */
    @ApiModelProperty(value = "账户编码")
    private String accountnumber;

    /**
     * 对方账号
     */
    @ApiModelProperty(value = "对方账号")
    private String oppositeaccountnumber;

    /**
     * 对方户名
     */
    @ApiModelProperty(value = "对方户名")
    private String oppositeaccountname;

}

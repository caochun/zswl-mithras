package cn.zswltech.mithras.dto.client.external.zhongdeng;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 中登网数据
 *
 * @author wangchuanhao
 * @date 2022/6/21 2:49 PM
 */
@Data
@ApiModel("中登网数据修改-请求体")
public class ZhongdengInfoModifyREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

    /**
     * 客户id
     */
    @NotNull
    @ApiModelProperty("客户id")
    private Long clientId;

    /**
     * 交易业务类型
     */
    @ApiModelProperty("交易业务类型")
    private String tradeBusinessType;

    /**
     * 授信机构
     */
    @ApiModelProperty("授信机构")
    private String creditOrg;

    /**
     * 金额
     */
    @ApiModelProperty("金额")
    private Long amount;

    /**
     * 登记日期
     */
    @ApiModelProperty("登记日期")
    private LocalDateTime regDate;

    /**
     * 登记到期日
     */
    @ApiModelProperty("登记到期日")
    private LocalDateTime regExpireDate;

    /**
     * 期限（年）
     */
    @ApiModelProperty("期限（年）")
    private Integer term;

}

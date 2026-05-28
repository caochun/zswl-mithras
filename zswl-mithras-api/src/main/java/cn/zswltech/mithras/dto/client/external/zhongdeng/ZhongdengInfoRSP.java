package cn.zswltech.mithras.dto.client.external.zhongdeng;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 中登网数据
 *
 * @author wangchuanhao
 * @date 2022/6/21 2:49 PM
 */
@Data
@ApiModel("中登网数据")
public class ZhongdengInfoRSP {

    @ApiModelProperty("id")
    private Long id;

    /**
     * 客户id
     */
    @ApiModelProperty("客户id")
    private String clientId;

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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime regDate;

    /**
     * 登记到期日
     */
    @ApiModelProperty("登记到期日")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime regExpireDate;

    /**
     * 期限（年）
     */
    @ApiModelProperty("期限（年）")
    private Integer term;

}

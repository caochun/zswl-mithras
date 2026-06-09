package cn.zswltech.mithras.liquiditymanage.mapper.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AccountSettingListQueryDto
 *
 * @author chenyifei
 * @since 2024/12/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountSettingListResultDTO {

    /**
     * Id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 融资类型
     */
    @ApiModelProperty("融资类型")
    private String financingType;

    /**
     * 融资id
     */
    @ApiModelProperty("融资id")
    private Long financingId;


    /**
     * 融资编号
     */
    @ApiModelProperty("融资编号")
    private String financingCode;

    /**
     * 融资机构 - 直融
     */
    @ApiModelProperty("融资机构名称")
    private String productName;

    /**
     * 融资金额
     */
    @ApiModelProperty("融资金额")
    private Long financingAmount;

    @ApiModelProperty(value = "资金经理")
    private Long fundManager;

    @ApiModelProperty(value = "资金经理姓名")
    private String fundManagerName;

    /**
     * 账户基本表id
     */
    @ApiModelProperty("账户基本表id")
    private Long accountId;

    /**
     * 账户类别
     */
    @ApiModelProperty("账户类别")
    private String accountCategory;

    /**
     * 开户银行
     */
    @ApiModelProperty("开户银行")
    private String accountBank;

    /**
     * 银行账号
     */
    @ApiModelProperty("银行账号")
    private String accountNumber;

    /**
     * 账户性质
     */
    @ApiModelProperty("账户性质")
    private String accountType;

    /**
     * 是否模拟结清
     */
    @ApiModelProperty("是否模拟结清")
    private Boolean simulateSettle;

    /**
     * 模拟结清日期
     */
    @ApiModelProperty("模拟结清日期")
    private LocalDate settleTime;

    /**
     * 模拟结清金额
     */
    @ApiModelProperty("模拟结清金额")
    private Long settleAmount;

    /**
     * 原账户id
     */
    @ApiModelProperty("原账户id")
    private Long accountOriginId;


}

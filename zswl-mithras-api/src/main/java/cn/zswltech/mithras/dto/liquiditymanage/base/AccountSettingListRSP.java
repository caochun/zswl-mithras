package cn.zswltech.mithras.dto.liquiditymanage.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;


/**
 * AccountSettingListRSP
 *
 * @author chenyifei
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "回款账户配置返回体")
public class AccountSettingListRSP {

    /**
     * Id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 融资id
     */
    @ApiModelProperty("融资id")
    private Long financingId;

    /**
     * 融资id
     */
    @ApiModelProperty("融资编号")
    private String financingCode;

    /**
     * 融资机构
     */
    @ApiModelProperty("融资机构id")
    private List<Long> organizationId;

    /**
     * 融资机构
     */
    @ApiModelProperty("融资机构名称")
    private List<String> organizationName;

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
     * 是否被编辑
     */
    @ApiModelProperty("是否被编辑")
    private Boolean isEdit;


}

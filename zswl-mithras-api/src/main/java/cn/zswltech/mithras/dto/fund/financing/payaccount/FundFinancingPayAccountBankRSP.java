package cn.zswltech.mithras.dto.fund.financing.payaccount;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @ClassName FundFinancingPayAccountBankREQ
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/2/21 10:04 上午
 * @Version 1.0
 **/
@Data
@ApiModel("融资管理-还款账户查询银行列表-返回体")
public class FundFinancingPayAccountBankRSP {
    /**
     * 主键id
     */
    @ApiModelProperty("基础数据-我方银行账户id")
    private Long bankAccountId;

    /**
     * 账户类型
     */
    @ApiModelProperty("账户类型")
    private String accountType;

    /**
     * 账户名称
     */
    @ApiModelProperty("账户名称")
    private String accountName;

    /**
     * 账号
     */
    @ApiModelProperty("账号")
    private String accountNumber;

    /**
     * 支行名称
     */
    @ApiModelProperty("支行名称")
    private String accountBank;

    /**
     * 开户时间
     */
    @ApiModelProperty("开户时间")
    private LocalDate accountOpeningDate;

    /**
     * 账户类别
     */
    @ApiModelProperty("账户类别")
    private String accountCategory;

}

package cn.zswltech.mithras.api.payment.writeoff;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/16 13:58
 */
@ApiModel("添加付款记录-入参")
@Data
public class ActualDetailPostReq {

    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("所属支付申请id")
    @NotNull(message = "paymentId 为null")
    private Long paymentId;

    @ApiModelProperty("所属合同id")
    private Long contractId;

    @ApiModelProperty("同步还是录入，显示‘财务系统’或者录入者名字")
    private String infoSource;

    @ApiModelProperty("付款方式")
    private String paymentMethod;

    @ApiModelProperty("实付日期")
    private LocalDate paidInDate;

    @ApiModelProperty("实付金额")
    private Long paidInAmount;

    @ApiModelProperty("附言")
    private String postscript;

    @ApiModelProperty("核销状态")
    private String writeOffStatus;

    @ApiModelProperty("附件名称")
    private String enclosureName;

    @ApiModelProperty("附件id")
    private Long enclosureId;

    @ApiModelProperty("our_account_id")
    private Long ourAccountId;
    @ApiModelProperty("our_account_name")
    private String ourAccountName;
    @ApiModelProperty("our_account_number")
    private String ourAccountNumber;
    @ApiModelProperty("our_account_bank")
    private String ourAccountBank;

    @ApiModelProperty("opposite_account_id")
    private Long oppositeAccountId;
    @ApiModelProperty("opposite_account_name")
    private String oppositeAccountName;
    @ApiModelProperty("opposite_account_number")
    private String oppositeAccountNumber;
    @ApiModelProperty("opposite_account_bank")
    private String oppositeAccountBank;
}

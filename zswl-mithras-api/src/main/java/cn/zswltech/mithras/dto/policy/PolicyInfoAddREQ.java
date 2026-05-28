package cn.zswltech.mithras.dto.policy;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@Data
@ApiModel("保单维护新增-请求体")
public class PolicyInfoAddREQ {

    @ApiModelProperty("付款id")
    private Long paymentId;

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty(value = "保单id")
    //@NotNull(message = "保单id")
    private Long policyId;

    @ApiModelProperty(value = "付款保单id")
    private Long paymentPolicyId;

    /**
    * 保单编号
    */
    @ApiModelProperty(value = "保单编号")
    private String policyCode;

    /**
     * 保险公司名称
     */
    @ApiModelProperty(value = "保险公司名称")
    private String insuranceCompany;

    /**
     * 保单种类
     * PolicyTypeEnum
     */
    @ApiModelProperty("保险种类")
    private String policyType;

    /**
    * 保单金额
    */
    @ApiModelProperty(value = "保单金额")
    private Long policyAmount;


    /**
     * 保险起始日
     */
    @ApiModelProperty(value = "保险起始日")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate insuranceStartDate;
    /**
    * 保险到期日
    */
    @ApiModelProperty(value = "保险到期日")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate insuranceEndDate;

    @ApiModelProperty(value = "是否续保 PolicyRenewInsuranceEnum")
    private String renewInsuranceFlag;

    /**
     * 标识信息
     */
    @ApiModelProperty(value = "标识信息")
    private String identificationInformation;

    /**
     * 备注
     */
    @ApiModelProperty("remark")
    private String remark;

    @ApiModelProperty("files")
    private MultipartFile[] files;

}

package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("保单维护编辑-请求体")
public class PolicyInfoModifyREQ {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty("付款id")
    private String paymentId;

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
    private LocalDate insuranceStartDate;

    /**
     * 保险到期日
     */
    @ApiModelProperty(value = "保险到期日")
    private LocalDate insuranceEndDate;

    @ApiModelProperty(value = "是否续保 PolicyRenewInsuranceEnum")
    private String renewInsuranceFlag;

    /**
     * 备注
     */
    @ApiModelProperty("remark")
    private String remark;

    /**
     * 标识信息
     */
    @ApiModelProperty(value = "标识信息")
    private String identificationInformation;

    @ApiModelProperty("files")
    private MultipartFile[] files;

    @ApiModelProperty("删除id")
    private List<Long> removeFileIds;


}

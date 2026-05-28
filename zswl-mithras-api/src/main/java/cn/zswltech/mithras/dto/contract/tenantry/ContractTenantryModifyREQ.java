package cn.zswltech.mithras.dto.contract.tenantry;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author vico
 * @description 合同-承租人/债权人/债务人表
 * @date 2022-08-12
 */
@Data
@ApiModel("合同-承租人/债权人/债务人表编辑-请求体")
public class ContractTenantryModifyREQ {

    /**
     * 租赁报价方案id
     */
    @ApiModelProperty(value = "承租人/债权人/债务人表id")
    private Long id;

    @ApiModelProperty(value = "合同id")
    @NotNull(message = "合同id不能为空")
    private Long contractId;

    /**
     * 承租人类型
     */
    @ApiModelProperty(value = "承租人/债权人/债务人类型")
    private String lesseeType;

    /**
     * 租赁物文件类型
     */
    @TableField("租赁物文件类型")
    private String leaseItemFileType;

    /**
     * 决议类型：股东会决议、董事会决议、股东决定、执行董事决定
     */
    @ApiModelProperty(value = "决议类型")
    private String resolutionType;

    @ApiModelProperty(value = "决议文件")
    private List<MultipartFile> fileList;

    @ApiModelProperty(value = "决议文件id")
    private List<Long> fileListId;

    /**
     * 存量风险敞口
     */
    @ApiModelProperty(value = "存量风险敞口")
    private Long stockRiskExposure;

    /**
     * 指定联系人
     */
    @ApiModelProperty(value = "指定联系人id")
    private Long contactId;

    /**
     * 是否上报征信 0不上报，1上报
     */
    @ApiModelProperty(value = "是否上报征信 0不上报，1上报")
    @NotNull(message = "是否上报征信不能为空")
    private Integer isReport;

    /**
     * 章程文件id
     */
    @ApiModelProperty(value = "章程文件id")
    private List<Long> constitutionFileIds;

    /**
     * 章程文件
     */
    @ApiModelProperty(value = "章程文件")
    private List<MultipartFile> multipartFileList;

    /**
     * 租金往来方
     */
    @ApiModelProperty(value = "租金往来方id")
    private String rentConcatAccountId;

    /**
     * 租金往来方
     */
    @ApiModelProperty(value = "租金往来方")
    private String rentConcatAccountName;
}

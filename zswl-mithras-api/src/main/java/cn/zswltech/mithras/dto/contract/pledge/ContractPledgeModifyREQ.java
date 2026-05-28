package cn.zswltech.mithras.dto.contract.pledge;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description 合同-质押措施
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同-质押措施编辑-请求体")
public class ContractPledgeModifyREQ {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    @NotNull(message = "质押措施id不能为空")
    private Long id;

    @ApiModelProperty(value = "合同id")
    @NotNull(message = "合同id不能为空")
    private Long contractId;

    @ApiModelProperty("质押合同编号")
    private String pledgeContractCode;

    /**
     * 关联合同编号
     */
    @ApiModelProperty(value = "关联合同编号")
    private List<String> relatContracts;

    @ApiModelProperty("质押物清单文件")
//    @NotNull(message = "质押物清单文件不能为空")
    private MultipartFile file;

    @ApiModelProperty("质押物清单文件id")
    private Long fileId;

    @ApiModelProperty(value = "质押人类型")
    private String pledgeType;

    /**
     * 质押人id，姓名，类型
     */
    @ApiModelProperty(value = "质押人id，姓名，类型")
    private List<Long> pledgeIds;

    /**
     * 质押物描述
     */
    @ApiModelProperty(value = "质押物描述")
    private String pledgeDescribe;

    @ApiModelProperty("是否最高额担保，0-否，1-是")
    @NotNull(message = "是否最高额不能为空")
    private Integer highest;

    @ApiModelProperty("质押类型")
    @NotBlank(message = "质押类型不能为空")
    private String contractPledgeType;

    @ApiModelProperty(value = "抵质押文件")
    private List<MultipartFile> mortgagePledgeFileList;

    @ApiModelProperty(value = "抵质押文件Ids")
    private List<Long> mortgagePledgeFileIds;
}

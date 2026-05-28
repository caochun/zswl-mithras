package cn.zswltech.mithras.dto.contract.mortgage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description 合同-抵押措施
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同-抵押措施新增-请求体")
public class ContractMortgageAddREQ {

    @ApiModelProperty("抵押物清单文件")
    private MultipartFile file;

    /**
    * 所属合同编号
    */
    @ApiModelProperty(value = "所属合同id")
    @NotNull(message = "所属合同id不能为空")
    private Long contractId;

    @ApiModelProperty("抵押合同编号")
    private String mortgageContractCode;

    /**
    * 关联合同编号
    */
    @ApiModelProperty(value = "关联合同编号")
    private List<String> relatContracts;

    @ApiModelProperty(value = "抵押人类型")
    @NotNull(message = "抵押人类型不能为空")
    private String mortgageType;

    /**
    * 抵押人id，姓名，类型
    */
    @ApiModelProperty(value = "抵押人id")
    @NotNull(message = "质押人不能为空")
    private List<Long> mortgageIds;

    /**
    * 抵押物描述
    */
    @ApiModelProperty(value = "抵押物描述")
    private String mortgageDescribe;

    @ApiModelProperty("是否评估，0-否，1-是")
    @NotNull(message = "是否评估不能为空")
    private Integer assess;

    @ApiModelProperty("评估日期，yyyy-MM-dd")
    private String assessDate;

    /**
     * 评估公司
     */
    @ApiModelProperty("评估公司")
    private String appraisalCompany;

    /**
     * 评估编号
     */
    @ApiModelProperty("评估编号")
    private String appraisalCode;

    @ApiModelProperty("是否最高额，0-否，1-是")
    @NotNull(message = "是否最高额不能为空")
    private Integer highest;

    @ApiModelProperty("抵押物类型")
    @NotBlank(message = "抵押物类型不能为空")
    private String mortgageItemType;

    @ApiModelProperty("抵押类型")
    @NotBlank(message = "抵押类型不能为空")
    private String contractMortgageType;

    @ApiModelProperty(value = "抵质押文件")
    private List<MultipartFile> mortgagePledgeFileList;
}

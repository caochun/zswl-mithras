package cn.zswltech.mithras.dto.contract.guarantor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description 合同-担保措施
 * @author vico
 * @date 2022-08-12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("合同-担保措施编辑-请求体")
public class ContractGuarantorModifyREQ extends ContractGuaranteeBasicREQ {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value = "合同id")
    @NotNull(message = "合同id不能为空")
    private Long contractId;


    /**
     * 关联合同编号
     */
    @ApiModelProperty(value = "关联合同编号")
    private List<String> relatContracts;

    /**
     * 担保人类型id，姓名
     */
    @ApiModelProperty(value = "担保人类型id")
    private List<Long> guarantorIds;

    @ApiModelProperty(value = "担保人类型")
    private String guarantorType;

    /**
     * 担保方式-连带责任担保、一般担保
     */
    @ApiModelProperty(value = "担保方式-连带责任担保、一般担保")
    private String guaranteeMethod;

    /**
     * 是否上报征信 0不上报，1上报
     */
    @ApiModelProperty(value = "是否上报征信 0不上报，1上报")
    private Integer isReport;

    @ApiModelProperty(value = "指定联系人id")
    private Long contactId;

    /**
     * 决议类型：股东会决议、董事会决议、股东决定、执行董事决定
     */
    @ApiModelProperty(value = "决议类型")
    private String resolutionType;
    /**
     * 决议文件
     */
    @ApiModelProperty(value = "决议文件")
    private List<MultipartFile> fileList;

    @ApiModelProperty(value = "决议文件id")
    private List<Long> fileListId;

}

package cn.zswltech.mithras.dto.contract.guarantor;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/10/11
 * @description
 */
@Data
public class ContractGuaranteeBasicREQ {
    @ApiModelProperty("联保标志，SINGLE-单人保证，MULTIPLE_SEPARATE-多人分保，JOINT-联保")
    @NotBlank(message = "联保标志不能为空")
    private String jointGuaranteeMark;

    @ApiModelProperty("担保金额，当联保标志为单人保证或者联保时填充该字段")
    private Long amountSingle;

    @ApiModelProperty("担保金额，当联保标志为多人分保时填充该字段")
    private String amountMultiple;

    @ApiModelProperty("保证合同编号")
    private String guarantorContractCode;

    @ApiModelProperty("章程文件id")
    private List<Long> constitutionFileList;

    @ApiModelProperty("章程文件")
    private List<MultipartFile> multipartFileList;
}

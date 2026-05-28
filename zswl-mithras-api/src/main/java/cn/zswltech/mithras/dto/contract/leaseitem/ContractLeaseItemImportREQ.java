package cn.zswltech.mithras.dto.contract.leaseitem;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/15
 * @description
 */
@Data
@ApiModel("租赁物清单导入-请求体")
public class ContractLeaseItemImportREQ {
    @NotNull(message = "导入文件不能为空")
    @ApiModelProperty("租赁物清单文件")
    private MultipartFile file;

    @NotNull(message = "主合同id不能为空")
    @ApiModelProperty("主合同id")
    private Long contractId;
}

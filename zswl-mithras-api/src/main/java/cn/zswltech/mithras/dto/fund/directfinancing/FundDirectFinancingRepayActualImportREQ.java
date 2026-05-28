package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zhaozhengkang
 * @description 直接融资-实际还款表
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-实际还款表新增-请求体")
public class FundDirectFinancingRepayActualImportREQ {
    @ApiModelProperty(value = "融资id")
    private Long financingId;

    @ApiModelProperty("文件")
    private MultipartFile file;

    @ApiModelProperty("是否校验利息差额")
    private Boolean isCheck;


}

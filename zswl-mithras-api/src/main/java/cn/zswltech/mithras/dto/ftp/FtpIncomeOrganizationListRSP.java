package cn.zswltech.mithras.dto.ftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 资金管理-融资管理-ftp收益记录表
 * @author vico
 * @date 2025-07-15
 */
@Data
@ApiModel("资金管理-融资管理-ftp收益记录表列表-请求体")
public class FtpIncomeOrganizationListRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机构名称")
    private String organizationName;

    @ApiModelProperty(value = "机构编号")
    private String organizationCode;

}

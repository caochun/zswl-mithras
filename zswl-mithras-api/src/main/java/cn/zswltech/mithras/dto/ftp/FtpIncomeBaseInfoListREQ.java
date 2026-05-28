package cn.zswltech.mithras.dto.ftp;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description 资金管理-融资管理-ftp收益表
 * @author vico
 * @date 2025-07-15
 */
@Data
@ApiModel("资金管理-融资管理-ftp收益表列表-请求体")
public class FtpIncomeBaseInfoListREQ extends PageReq {

    @ApiModelProperty("融资编号")
    private String financingCode;

    @ApiModelProperty("融资机构id")
    private Long organizationId;

    @ApiModelProperty("资金主办")
    private Long fundManagerId;

    @ApiModelProperty("融资id")
    private List<Long> financingIds;

}

package cn.zswltech.mithras.dto.ftp;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/11 15:31
 */
@Data
@ApiModel("月度计价指导列表-返回体")
public class FtpQuarterlyGuidanceRsp extends ListBaseRSP {
    private Integer year;
    private Integer quarter;
    private String processStatus;
}

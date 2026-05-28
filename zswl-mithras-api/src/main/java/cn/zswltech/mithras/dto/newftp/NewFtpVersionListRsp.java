package cn.zswltech.mithras.dto.newftp;

import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/24 10:43
 */
@Data
public class NewFtpVersionListRsp extends CommonVersionListRSP {

    @ApiModelProperty("月份")
    private LocalDate month;
    
}

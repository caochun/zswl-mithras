package cn.zswltech.mithras.dto.riskcontrol.opinion;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author yangxiong
 * @date 2024/8/30/11:28
 * @description
 */
@Data
public class OpinionMonitorCloseREQ {

    @ApiModelProperty(value = "待发起舆情ID")
    private Long id;

    @ApiModelProperty(value = "关闭原因")
    private String closeReason;

}

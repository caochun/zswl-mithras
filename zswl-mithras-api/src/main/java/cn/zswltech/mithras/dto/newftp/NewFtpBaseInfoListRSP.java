package cn.zswltech.mithras.dto.newftp;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description ftp主表
 * @author zhaozhengkang
 * @date 2023-05-21
 */
@Data
@ApiModel("ftp主表列表-返回体")
public class NewFtpBaseInfoListRSP {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "定价频率")
    private String pricingFrequency;

    @ApiModelProperty(value = "所属月份")
    private LocalDate month;

    @ApiModelProperty(value = "创建人")
    private String createByName;

    @ApiModelProperty(value = "状态")
    private String ftpStatus;

    @TableField("流程状态")
    private String ftpProcessStatus;

    @TableField("创建时间")
    private LocalDateTime createTime;

    @TableField("生效时间")
    private LocalDateTime effectTime;

}

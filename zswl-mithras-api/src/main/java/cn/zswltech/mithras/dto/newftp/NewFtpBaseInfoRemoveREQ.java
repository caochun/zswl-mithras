package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
/**
 * @description ftp主表
 * @author zhaozhengkang
 * @date 2023-05-21
 */
@Data
@ApiModel("ftp主表删除-请求体")
public class NewFtpBaseInfoRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}

package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author yibin
 */
@Data
@ApiModel("获取指定用户负责的客户列表-请求体")
public class SponsorClientListREQ implements Serializable {

    @NotNull
    @ApiModelProperty("客户经理id")
    private Long belongSponsorId;


    @NotNull
    @ApiModelProperty("所属部门id")
    private Long belongDeptId;

}

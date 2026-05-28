package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author yibin
 */
@Data
@ApiModel("取消操作新的提交转移编辑用户负责的客户-请求体")
public class SponsorClientRemoveNewREQ implements Serializable {

        @NotNull
        @ApiModelProperty("客户经理id")
        private Long belongSponsorId;


        @NotNull
        @ApiModelProperty("所属部门id")
        private Long belongDeptId;

}

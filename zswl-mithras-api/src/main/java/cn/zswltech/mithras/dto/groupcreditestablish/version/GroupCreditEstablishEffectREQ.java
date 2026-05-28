package cn.zswltech.mithras.dto.groupcreditestablish.version;

import cn.zswltech.mithras.dto.process.modify.remark.ProcessModifyRemarkAddREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author wangchuanhao
 * @description 集团授信立项基本信息表
 * @date 2022-11-11
 */
@Data
@ApiModel(value = "集团授信立项生效-请求体")
public class GroupCreditEstablishEffectREQ {
    @NotNull
    @ApiModelProperty("立项Id")
    public Long id;

    @NotNull
    @ApiModelProperty("只是检验是否能提交审批")
    private Boolean onlyCheck = false;

    @ApiModelProperty("变更说明")
    private ProcessModifyRemarkAddREQ remarkAddREQ;
}

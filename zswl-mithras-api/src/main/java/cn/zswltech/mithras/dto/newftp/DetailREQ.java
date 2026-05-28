package cn.zswltech.mithras.dto.newftp;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author yangxiong
 * @date 2024/3/25/09:57
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DetailREQ extends PageReq {

    @ApiModelProperty(value = "ftpId")
    @NotNull(message = "关联ftpId不能为空")
    private Long mainId;
}

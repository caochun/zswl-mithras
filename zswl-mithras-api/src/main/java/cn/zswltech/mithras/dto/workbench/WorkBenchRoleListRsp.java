package cn.zswltech.mithras.dto.workbench;

import cn.zswltech.mithras.dto.SelectRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/8/17 09:59
 */
@Data
@ApiModel("工作台-卡片指标列表-返回体")
public class WorkBenchRoleListRsp {
    @ApiModelProperty(value = "默认角色")
    private SelectRSP defaultRole;

    @ApiModelProperty(value = "角色列表")
    private List<SelectRSP> roleList;
}

package cn.zswltech.mithras.dto.projestablish.baseinfo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 创建审批之前的立项查询接口返回体
 * @author: zhaozhengkang
 * @date: 2022/8/8 16:01
 */
@ApiModel("创建审批之前的立项查询接口返回体")
@Data
public class ProjEstablishVagueListRSP {

    @ApiModelProperty("立项项目Id")
    private Long id;

    @ApiModelProperty("立项项目名称")
    private String projName;

    @ApiModelProperty("客户名称")
    private List<String> clientNames;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("项目状态 TAKE_EFFECT为生效")
    private String projReviewStatus;
}

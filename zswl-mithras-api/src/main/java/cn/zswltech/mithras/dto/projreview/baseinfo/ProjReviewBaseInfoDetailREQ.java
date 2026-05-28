package cn.zswltech.mithras.dto.projreview.baseinfo;

import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 立项基本信息表
 * @author zhaozhengkang
 * @date 2022-08-02
 */
@Data
@ApiModel("项目评审基本信息详情查询-请求体")
@AllArgsConstructor
@NoArgsConstructor
public class ProjReviewBaseInfoDetailREQ extends VersionBaseREQ {
    @ApiModelProperty("project review Id")
    private Long id;

    @ApiModelProperty("流程ID")
    private String processInstanceId;
}

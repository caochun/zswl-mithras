package cn.zswltech.mithras.dto.projestablish.version;
import lombok.Data;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 立项信息版本表
 * @author zhaozhengkang
 * @date 2022-07-19
 */
@Data
@ApiModel("立项信息版本表列表-请求体")
public class ProjEstablishVersionListREQ extends PageReq {
    @ApiModelProperty("所属立项Id")
    private Long projEstablishId;
}

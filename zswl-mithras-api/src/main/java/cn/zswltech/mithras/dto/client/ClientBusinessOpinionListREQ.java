package cn.zswltech.mithras.dto.client;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import lombok.Data;
/**
 * @description 客户工商信息处理意见表
 * @author vico
 * @date 2023-09-11
 */
@Data
@ApiModel("客户工商信息处理意见表列表-请求体")
public class ClientBusinessOpinionListREQ extends PageReq {
    private String contractId;
}

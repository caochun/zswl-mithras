package cn.zswltech.mithras.dto.riskcontrol.opinion;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @ClassName RiskControlOpinionListRsp
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/6 6:54 下午
 * @Version 1.0
 **/
@Data
@ApiModel("风控舆情变动回调-请求体")
public class RiskControlOpinionNoticeReq {

    //起始id
    private Integer startId;

    //数量
    private Integer size;

}

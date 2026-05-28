package cn.zswltech.mithras.dto.riskcontrol.opinion;

import lombok.Data;

/**
 * @ClassName RiskControlOpinionListRsp
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/6 6:54 下午
 * @Version 1.0
 **/
@Data
public class RiskControlOpinionListReq {

    //客户名称
    private String chiName;

    //统一社会信用代码
    private String creditCode;
}

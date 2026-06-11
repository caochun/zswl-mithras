package cn.zswltech.mithras.third.riskopinion.client.req;

import lombok.Data;

/**
 * @ClassName RiskControlOpinionListRsp
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/6 6:54 下午
 * @Version 1.0
 **/
@Data
public class RiskControlOpinionPullListREQ {

    //拉取当前id后的数据，不包含此id
    private Integer id;

    //每次拉取的数据量
    private Integer size;

}

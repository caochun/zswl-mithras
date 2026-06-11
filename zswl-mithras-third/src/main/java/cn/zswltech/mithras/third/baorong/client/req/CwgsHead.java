package cn.zswltech.mithras.third.baorong.client.req;

import lombok.Data;

@Data
public class CwgsHead {
    private String serviceCode;//服务代码
    private String serviceNo;//服务编号
    private String consumerCode;//消费端编码
    private String channelType;//渠道标志 默认为ESB
    private String consumerId;//发起方系统编号 调用方系统编码
    private String reqSequence;//渠道流水号 服务编号+消费端编码+时间戳
    private String trandate;//服务请求系统的日期，格式为YYYYMMDD
    private String trantime;//服务请求系统的时间，格式为HHMMSS
    private String returnMsg;
    private Integer returnCode;
}
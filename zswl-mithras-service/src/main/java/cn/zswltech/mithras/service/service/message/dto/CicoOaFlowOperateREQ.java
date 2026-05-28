package cn.zswltech.mithras.service.service.message.dto;

import lombok.Data;

/**
 * @ClassName CicoOaListReq
 * @Description TODO
 * @Author jackerhe
 * @Date 2025/1/17 11:19
 * @Version 1.0
 **/
@Data
public class CicoOaFlowOperateREQ {
    private String flowid;
    private String receiver;
    private String syscode;
    private String appurl;
    private String pcurl;
    private String requestname;
    private String createdatetime;
    private String creator;
    private String isremark;
    private String receivedatetime;
    private String receivets;
    private String viewtype;
    private String workflowname;
}

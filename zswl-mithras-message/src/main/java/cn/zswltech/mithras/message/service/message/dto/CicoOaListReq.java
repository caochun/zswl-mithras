package cn.zswltech.mithras.message.service.dto;

import lombok.Data;

/**
 * @ClassName CicoOaListReq
 * @Description TODO
 * @Author jackerhe
 * @Date 2025/1/17 11:19
 * @Version 1.0
 **/
@Data
public class CicoOaListReq {
    //异构系统标识
    private String syscode;
    //接收人
    private String receiver;
    //流程实例id
    //private String flowid;
    //流程处理状态
    //0：待办
    //2：已办
    //4：办结
    //8：抄送（待阅）
    private Integer isremark;

    private Integer pagenum;
    private Integer pagesize;
    //创建日期-（开始日期，区间范围）
    private String createdates;
    //创建日期-（结束日期，区间范围）
    private String createdatee;
}

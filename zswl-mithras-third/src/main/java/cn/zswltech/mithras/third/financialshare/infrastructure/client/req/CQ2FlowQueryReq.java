package cn.zswltech.mithras.third.financialshare.infrastructure.client.req;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName FlowQueryReq
 * @Description 查询流水入参
 * @Author jackerhe
 * @Date 2024/5/14 2:47 下午
 * @Version 1.0
 **/
@Data
public class CQ2FlowQueryReq {
    private String requestId;
    private LocalDateTime begintime; // 开始时间
    private LocalDateTime endtime; // 结束时间
    private String companynumber; // 组织编码
    //private String validcode; // 验证码无
    private Integer pageSize;
    private Integer pageNo;
}
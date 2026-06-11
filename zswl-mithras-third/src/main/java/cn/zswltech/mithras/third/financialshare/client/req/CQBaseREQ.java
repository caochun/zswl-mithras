package cn.zswltech.mithras.third.financialshare.client.req;

import lombok.Data;

/**
 * @ClassName CQBaseREQ
 * @Description
 * @Author jackerhe
 * @Date 2022/11/1 11:34 上午
 * @Version 1.0
 **/
@Data
public class CQBaseREQ {
    //来源系统单号
    private String sourcebillno;

    //人员编码
    private String creator;

    //单据日期 2022.6.26
    private String bizdate;
}

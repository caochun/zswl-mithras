package cn.zswltech.mithras.third.service.financial.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author yibin
 */
@Accessors(chain = true)
@Data
public class SyncCqReqBizInfo {
    private String orgCode;
    private String customer;
    private String customerName;
    private String projSponsorUserPhone;
    private String contractCode;
    //税率
    private BigDecimal rate;
    private String bizType;
    private String leaseType;
    private String interestWay;
}

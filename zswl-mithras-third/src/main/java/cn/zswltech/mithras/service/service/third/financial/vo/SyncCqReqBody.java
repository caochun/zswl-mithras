package cn.zswltech.mithras.service.service.third.financial.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
@Accessors(chain = true)
public class SyncCqReqBody {
    /**
     * 日期
     */
    private LocalDate date;

    private Integer phase;

    private Long rent;

    //收款编号
    private String code;

    //业务只需要关注本次请求发送数据，应在发送请求前获取上次请求数据时比对
    //private Long rentDiff;

    private Long principal;

    //private Long principalDiff;

    private Long interest;

   //private Long interestDiff;

    //借据中剩余本金
    private Long remainingPrincipal;

    public SyncCqReqBody() {
    }
}

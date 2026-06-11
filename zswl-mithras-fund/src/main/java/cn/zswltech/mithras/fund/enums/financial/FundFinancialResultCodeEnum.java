package cn.zswltech.mithras.fund.enums.financial;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * @author zswl
 */
@Getter
@AllArgsConstructor
public enum FundFinancialResultCodeEnum implements PullDown {

    SUCCESS("贷款合同推送",0),
    REQUEST_EXCEPTION("无对应接口映射信息查询，请确认",2),
    QUERY_EXCEPTION("系统异常,请求条件参数与系统不符",4),
    AUTH_EXCEPTION("验签失败",1),
    ;

    private String display;
    private Integer code;



    @Override
    public String display() {
        return display;
    }
}

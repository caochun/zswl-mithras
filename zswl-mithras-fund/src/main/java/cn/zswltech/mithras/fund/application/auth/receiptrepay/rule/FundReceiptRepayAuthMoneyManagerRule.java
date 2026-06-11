package cn.zswltech.mithras.fund.application.auth.receiptrepay.rule;

import cn.zswltech.mithras.foundation.auth.checker.AuthHelper;
import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.CurrentUserJobResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 判断当前操作数据 是不是 资金经理
 *
 * @author wangchuanhao
 * @date 2022/7/21 11:52 PM
 */
@Component
@Slf4j
public class FundReceiptRepayAuthMoneyManagerRule {

    @Resource
    private AuthHelper authHelper;
    @Resource
    private CurrentUserJobResolver currentUserJobResolver;

    public void check(DataAuthBusinessModule businessModule, Long mainId) {
        if (!currentUserJobResolver.currentUserIsSpecificJob("moneymanager")) {
            throw new MithrasException("只有资金经理才能操作数据!");
        }
    }

}

package cn.zswltech.mithras.fund.application.auth.receiptrepay;


import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.fund.application.auth.receiptrepay.rule.FundReceiptRepayAuthMoneyManagerRule;
import cn.zswltech.mithras.fund.application.auth.receiptrepay.rule.FundReceiptRepayAuthProcessRule;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 资金收付款权限
 * @author wangchuanhao
 * @date 2023/2/24
 * @description 主表修改权限校验
 */
@Component
public class FundReceiptRepayModifyMainAuthChecker implements IDataAuthChecker {

    @Resource
    private FundReceiptRepayAuthProcessRule fundReceiptRepayAuthProcessRule;
    @Resource
    private FundReceiptRepayAuthMoneyManagerRule fundReceiptRepayAuthMoneyManagerRule;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        if (Objects.isNull(keyId)) {
            throw new AuthCheckException("id不能为空");
        }
        fundReceiptRepayAuthMoneyManagerRule.check(businessModule, keyId);
        fundReceiptRepayAuthProcessRule.check(businessModule, keyId);
        return true;
    }

}

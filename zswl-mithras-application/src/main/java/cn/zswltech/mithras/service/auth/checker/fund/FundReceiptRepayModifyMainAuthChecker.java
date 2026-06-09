package cn.zswltech.mithras.service.auth.checker.fund;


import cn.zswltech.mithras.service.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.rule.fund.FundReceiptRepayAuthMoneyManagerRule;
import cn.zswltech.mithras.service.auth.rule.fund.FundReceiptRepayAuthProcessRule;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
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

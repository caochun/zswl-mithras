package cn.zswltech.mithras.service.auth.checker.fund;


import cn.zswltech.mithras.service.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.service.auth.checker.AuthHelper;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.rule.fund.FundReceiptRepayAuthMoneyManagerRule;
import cn.zswltech.mithras.service.auth.rule.fund.FundReceiptRepayAuthProcessRule;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 资金收付款权限
 * 子表数据删除权限验证
 * 可用于需转一层才能拿到主表id的
 *
 * @author wangchuanhao
 * @date 2023/2/24 12:05 AM
 */
@Component
@Slf4j
public class FundReceiptRepayRemoveSubAuthChecker implements IDataAuthChecker {
    @Resource
    private FundReceiptRepayAuthProcessRule fundReceiptRepayAuthProcessRule;
    @Resource
    private FundReceiptRepayAuthMoneyManagerRule fundReceiptRepayAuthMoneyManagerRule;
    @Resource
    private AuthHelper authHelper;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        if (Objects.isNull(keyId)) {
            throw new AuthCheckException("id不能为空");
        }
        // 从数据库子表中获取主表id
        Long mainId = authHelper.getMainIdFromSubTable(businessModule, helperMapperClass, keyId);
        // 权限校验
        fundReceiptRepayAuthMoneyManagerRule.check(businessModule, mainId);
        fundReceiptRepayAuthProcessRule.check(businessModule, mainId);
        return true;
    }

}

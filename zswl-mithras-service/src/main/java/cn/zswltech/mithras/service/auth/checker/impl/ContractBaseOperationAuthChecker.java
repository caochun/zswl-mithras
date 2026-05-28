package cn.zswltech.mithras.service.auth.checker.impl;

import cn.hutool.core.lang.Assert;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/8/31
 * @description
 */
@Component
@Deprecated
public class ContractBaseOperationAuthChecker implements IDataAuthChecker {
    @Resource
    private DataAuthProcessRule dataAuthProcessRule;

    @Override
    public boolean check(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        BaseMapper<?> baseMapper = SpringContextHolder.getApplicationContext().getBean(businessModule.getMainMapperClass());
        Object mainData = baseMapper.selectById(keyId);
        ContractBaseInfo contractBaseInfo;
        if (mainData instanceof ContractBaseInfo) {
            contractBaseInfo = (ContractBaseInfo) mainData;
        } else {
            throw new MithrasException("主表数据类型错误");
        }
        // 判断合同是否结清
        Assert.isTrue(!Objects.equals(ContractStatus.SETTLE.name(), contractBaseInfo.getContractStatus()), () -> MithrasException.newException("合同已结清，无法操作"));
        // 校验当前用户是否项目主办
        Assert.isTrue(Objects.equals(AccountUtil.getLoginInfo().getId(), contractBaseInfo.getProjSponsorUserId()), () -> AuthCheckException.newException("只有主办能够操作"));
        // 流程状态校验
        dataAuthProcessRule.check(businessModule, keyId);
        return true;
    }

    @Override
    public boolean checkBatch(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIds, Object[] args) {
        return false;
    }
}

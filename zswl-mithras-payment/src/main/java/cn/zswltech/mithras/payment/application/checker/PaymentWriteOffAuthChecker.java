package cn.zswltech.mithras.payment.application.checker;


import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.CurrentUserJobResolver;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/25 11:32
 */
@Component
public class PaymentWriteOffAuthChecker implements IDataAuthChecker {
    @Resource
    private CurrentUserJobResolver currentUserJobResolver;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        List<String> jobs = currentUserJobResolver.queryUserJobList(AccountUtil.getLoginInfo().getId());
        boolean canWriteOff = false;
        for (String job : jobs) {
            if(job.equals(JobEnum.financialmanager.name()) || job.equals(JobEnum.cashier.name())){
                canWriteOff = true;
            }
        }
        if(!canWriteOff){
            throw new MithrasException("财务经理或出纳才可执行操作");
        }
        return true;
    }

    @Override
    public boolean checkBatch(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIds, Object[] args) {
        return false;
    }
}

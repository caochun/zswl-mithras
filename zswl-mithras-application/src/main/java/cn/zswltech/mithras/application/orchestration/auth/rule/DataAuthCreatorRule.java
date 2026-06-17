package cn.zswltech.mithras.application.orchestration.auth.rule;


import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.auth.DataAuthCreatorGuard;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/2/24
 * @description
 */
@Component
public class DataAuthCreatorRule implements DataAuthCreatorGuard {
    @Override
    public void check(DataAuthBusinessModule businessModule, Long mainId) {
        BaseMapper mainTableMapper = SpringContextHolder.getBean(businessModule.getMainMapperClass());
        Object mainObject = mainTableMapper.selectById(mainId);
        if (Objects.isNull(mainObject)) {
            throw new AuthCheckException("主表数据不存在");
        }
        Long maintainerId = (Long) ReflectUtil.getFieldValue(mainObject, "createBy");
        if (null == maintainerId) {
            return;
        }
        if (!Objects.equals(maintainerId, AccountUtil.getLoginInfo().getId())) {
            throw new AuthCheckException("非数据创建人，不支持该种操作");
        }
    }
}

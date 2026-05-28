/*
package cn.zswltech.mithras.service.controller.projestablish.checker;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.checker.impl.CommonViewMainAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.projestablish.ProjEstablishAocPriceMapper;
import cn.zswltech.mithras.service.mapper.projestablish.ProjEstablishFactoringPriceMapper;
import cn.zswltech.mithras.service.mapper.projestablish.ProjEstablishLeasePriceMapper;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Objects;

*/
/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/16 19:26
 *//*

@Component
public class ProjEstablishViewSubAuthChecker implements IDataAuthChecker {
    @Resource
    private CommonViewMainAuthChecker commonViewMainAuthChecker;

    @Override
    public boolean check(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        BaseMapper subTableMapper = SpringContextHolder.getBean(helperMapperClass);
        Object subData = null;
        if(subTableMapper instanceof ProjEstablishAocPriceMapper){
            subData = ((ProjEstablishAocPriceMapper) subTableMapper).selectByMainId(keyId);
        }else if(subTableMapper instanceof ProjEstablishFactoringPriceMapper){
            subData = ((ProjEstablishFactoringPriceMapper) subTableMapper).selectByMainId(keyId);
        }else if(subTableMapper instanceof ProjEstablishLeasePriceMapper){
            subData = ((ProjEstablishLeasePriceMapper) subTableMapper).selectByMainId(keyId);
        }
        if (Objects.isNull(subData)) {
            return true;
        }
        // 通过子表字段查询主表
        Long mainId = (Long) ReflectUtil.getFieldValue(subData, businessModule.getSubTableMainIdFieldName());
        return commonViewMainAuthChecker.check(businessModule, helperMapperClass, mainId, args);
    }

    @Override
    public boolean checkBatch(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIds, Object[] args) {
        throw new AuthCheckException("暂不支持该种类型的权限校验操作");
    }
}
*/

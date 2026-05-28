package cn.zswltech.mithras.service.auth.checker;

import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;

/**
 * 数据权限校验器接口
 *
 * @author wangchuanhao
 * @date 2022/7/21 1:51 PM
 */
public interface IDataAuthChecker {

    /**
     * 业务模块
     * @param businessModule
     * @param helperMapperClass
     * @param keyId
     */
    default boolean check(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        throw new AuthCheckException("暂不支持该种类型的权限校验操作");
    }

    default boolean checkBatch(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIds, Object[] args) {
        throw new AuthCheckException("暂不支持该种类型的权限校验操作");
    }
}

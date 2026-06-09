package cn.zswltech.mithras.afterlease.application;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface AfterLeaseModifyAuthPort {
    void check(String businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args);
}

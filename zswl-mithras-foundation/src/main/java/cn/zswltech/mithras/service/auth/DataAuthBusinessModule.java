package cn.zswltech.mithras.service.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * Lightweight view of a business module used by data authorization.
 */
public interface DataAuthBusinessModule {

    String name();

    List<String> getModelKeyList();

    Class<? extends BaseMapper> getMainMapperClass();

    String getSubTableMainIdFieldName();

    String getSponsorModule();

    String getSponsorModuleIdFieldName();
}

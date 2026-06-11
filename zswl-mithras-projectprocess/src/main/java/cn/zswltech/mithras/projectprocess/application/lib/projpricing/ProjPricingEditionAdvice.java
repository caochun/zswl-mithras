package cn.zswltech.mithras.projectprocess.application.lib.projpricing;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;

/**
 * @ClassName ProjReviewEditionAdvice
 * @Description
 * @Author jackerhe
 * @Date 2022/9/6 10:12 上午
 * @Version 1.0
 **/
public class ProjPricingEditionAdvice<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    private static final String PROJ_PRICING_MODULE = "PROJ_PRICING";

    @Resource
    private CommonVersionMapper commonVersionMapper;

    public CommonVersion oldVersion(Long mainId) {
        if( ObjectUtil.isNull(mainId)){
            return null;
        }
        CommonVersion commonVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, mainId)
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CommonVersion::getModule, PROJ_PRICING_MODULE)
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1"));
        if(ObjectUtil.isNull(commonVersion)){
            throw new MithrasException("此项目定价未生效");
        }
        // 处理租赁
        return commonVersion;
    }

}

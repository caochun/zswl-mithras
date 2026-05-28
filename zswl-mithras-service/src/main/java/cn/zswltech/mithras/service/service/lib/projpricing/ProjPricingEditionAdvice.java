package cn.zswltech.mithras.service.service.lib.projpricing;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @ClassName ProjReviewEditionAdvice
 * @Description
 * @Author jackerhe
 * @Date 2022/9/6 10:12 上午
 * @Version 1.0
 **/
public class ProjPricingEditionAdvice<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    public CommonVersion oldVersion(Long mainId) {
        if( ObjectUtil.isNull(mainId)){
            return null;
        }
        CommonVersionMapper commonVersionMapper = SpringContextHolder.getBean(CommonVersionMapper.class);
        CommonVersion commonVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, mainId)
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CommonVersion::getModule, BusinessModuleEnum.PROJ_PRICING.name())
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1"));
        if(ObjectUtil.isNull(commonVersion)){
            throw new MithrasException("此项目定价未生效");
        }
        // 处理租赁
        return commonVersion;
    }

}

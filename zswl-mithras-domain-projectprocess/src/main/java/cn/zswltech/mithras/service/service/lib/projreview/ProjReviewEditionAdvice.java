package cn.zswltech.mithras.service.service.lib.projreview;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.others.MithrasException;
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
public class ProjReviewEditionAdvice<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    private static final String PROJ_REVIEW_MODULE = "PROJ_REVIEW";

    @Resource
    private CommonVersionMapper commonVersionMapper;

    public CommonVersion oldVersion(Long mainId) {
        if( ObjectUtil.isNull(mainId)){
            return null;
        }
        CommonVersion commonVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, mainId)
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CommonVersion::getModule, PROJ_REVIEW_MODULE)
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1"));
        if(ObjectUtil.isNull(commonVersion)){
            throw new MithrasException("此项目评审未生效");
        }
        // 处理租赁
        return commonVersion;
    }

}

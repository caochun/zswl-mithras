package cn.zswltech.mithras.service.service.lib.projreview;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.common.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.contract.ContractLeasePriceLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @ClassName ProjReviewEditionAdvice
 * @Description
 * @Author jackerhe
 * @Date 2022/9/6 10:12 上午
 * @Version 1.0
 **/
public class ProjReviewEditionAdvice<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    public CommonVersion oldVersion(Long mainId) {
        if( ObjectUtil.isNull(mainId)){
            return null;
        }
        CommonVersionMapper commonVersionMapper = SpringContextHolder.getBean(CommonVersionMapper.class);
        CommonVersion commonVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, mainId)
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CommonVersion::getModule, BusinessModuleEnum.PROJ_REVIEW.name())
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1"));
        if(ObjectUtil.isNull(commonVersion)){
            throw new MithrasException("此项目评审未生效");
        }
        // 处理租赁
        return commonVersion;
    }

}

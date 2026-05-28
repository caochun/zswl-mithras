package cn.zswltech.mithras.service.service.lib.appraisalcompanywhitelist;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.leaseholdproperty.AppraisalCompanyDetailRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.model.leaseholdproperty.AppraisalCompanyWhitelist;
import cn.zswltech.mithras.service.mapper.model.leaseholdproperty.AppraisalCompanyWhitelistLib;
import cn.zswltech.mithras.service.service.lib.LibAbstractHandler;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

/**
 * @author dingqi
 * @date 2025/9/4
 * @description
 */
@Component
public class AppraisalCompanyWhitelistLibHandler extends LibAbstractHandler<AppraisalCompanyWhitelistLib, AppraisalCompanyWhitelist, AppraisalCompanyDetailRSP> {
    @Override
    protected AppraisalCompanyWhitelistLib entity2Lib(AppraisalCompanyWhitelist f) {
        return BeanUtil.copyProperties(f, AppraisalCompanyWhitelistLib.class);
    }

    @Override
    protected AppraisalCompanyWhitelist lib2Entity(AppraisalCompanyWhitelistLib t) {
        return BeanUtil.copyProperties(t, AppraisalCompanyWhitelist.class);
    }

    @Override
    protected AppraisalCompanyDetailRSP lib2Rsp(AppraisalCompanyWhitelistLib f) {
        return BeanUtil.copyProperties(f, AppraisalCompanyDetailRSP.class);
    }

    @Override
    public String libMainIdFieldName() {
        return "origin_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "id";
    }

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.emptySet();
    }

    @Override
    public BusinessModuleEnum businessModuleEnum() {
        return BusinessModuleEnum.APPRAISAL_COMPANY_WHITELIST;
    }
}

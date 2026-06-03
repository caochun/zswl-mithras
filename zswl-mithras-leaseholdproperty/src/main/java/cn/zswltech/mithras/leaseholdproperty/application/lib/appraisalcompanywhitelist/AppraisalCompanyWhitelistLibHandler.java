package cn.zswltech.mithras.leaseholdproperty.application.lib.appraisalcompanywhitelist;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.leaseholdproperty.AppraisalCompanyDetailRSP;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.model.AppraisalCompanyWhitelist;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.model.AppraisalCompanyWhitelistLib;
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
    protected String businessModuleName() {
        return "APPRAISAL_COMPANY_WHITELIST";
    }
}

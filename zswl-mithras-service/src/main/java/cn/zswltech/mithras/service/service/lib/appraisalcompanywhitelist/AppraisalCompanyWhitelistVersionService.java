package cn.zswltech.mithras.service.service.lib.appraisalcompanywhitelist;

import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.leaseholdproperty.AppraisalCompanyWhitelist;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author dingqi
 * @date 2025/9/4
 * @description
 */
@Service
public class AppraisalCompanyWhitelistVersionService extends CommonVersionService<AppraisalCompanyWhitelist> {
    @Resource
    private AppraisalCompanyWhitelistLibHandler appraisalCompanyWhitelistLibHandler;
    @Resource
    private AppraisalCompanyWhitelistMaterialLibHandler appraisalCompanyWhitelistMaterialLibHandler;

    @Override
    public void customFlushData(AppraisalCompanyWhitelist appraisalCompanyWhitelist, String version, boolean needClearLastFlag, Integer versionType) {
        appraisalCompanyWhitelistLibHandler.flushData(version, appraisalCompanyWhitelist.getId(), needClearLastFlag, versionType);
        appraisalCompanyWhitelistMaterialLibHandler.flushData(version, appraisalCompanyWhitelist.getId(), needClearLastFlag, versionType);
    }

    @Override
    public void customReset(AppraisalCompanyWhitelist appraisalCompanyWhitelist, CommonVersion commonVersion) {
        appraisalCompanyWhitelistLibHandler.reset(appraisalCompanyWhitelist.getId(), commonVersion.getVersion());
        appraisalCompanyWhitelistMaterialLibHandler.reset(appraisalCompanyWhitelist.getId(), commonVersion.getVersion());
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        throw new MithrasException("暂不支持的功能");
    }

    @Override
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, AppraisalCompanyWhitelist baseModel, Map<Long, String> userNameMap) {
        throw new MithrasException("暂不支持的功能");
    }

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.APPRAISAL_COMPANY_WHITELIST;
    }
}

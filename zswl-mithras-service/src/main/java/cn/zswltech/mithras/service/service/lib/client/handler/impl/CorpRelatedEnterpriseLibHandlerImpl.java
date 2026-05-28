package cn.zswltech.mithras.service.service.lib.client.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseListRSP;
import cn.zswltech.mithras.service.enums.InfoModule;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.mapper.corp.IndustryTypeMapper;
import cn.zswltech.mithras.service.mapper.model.client.CorpAddressInfo;
import cn.zswltech.mithras.service.mapper.model.client.CorpRelatedEnterprise;
import cn.zswltech.mithras.service.mapper.model.client.CorpRelatedEnterpriseLib;
import cn.zswltech.mithras.service.mapper.model.client.IndustryType;
import cn.zswltech.mithras.service.service.lib.client.handler.ClientLibAbstractHandler;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Service
public class CorpRelatedEnterpriseLibHandlerImpl extends ClientLibAbstractHandler<CorpRelatedEnterpriseLib, CorpRelatedEnterprise, CorpRelatedEnterpriseListRSP> {

    @Resource
    private IndustryTypeMapper industryTypeMapper;

    @Override
    protected CorpRelatedEnterpriseLib entity2Lib(CorpRelatedEnterprise corpRelatedEnterprise) {
        CorpRelatedEnterpriseLib corpRelatedEnterpriseLib = BeanUtil.copyProperties(corpRelatedEnterprise, CorpRelatedEnterpriseLib.class);
        return corpRelatedEnterpriseLib;
    }

    @Override
    protected CorpRelatedEnterprise lib2Entity(CorpRelatedEnterpriseLib corpRelatedEnterpriseLib) {
        CorpRelatedEnterprise corpRelatedEnterprise = BeanUtil.copyProperties(corpRelatedEnterpriseLib, CorpRelatedEnterprise.class);
        return corpRelatedEnterprise;
    }

    @Override
    public boolean needHandle(Long clientId, ClientType clientType) {
        return ClientType.CORPORATION.equals(clientType);
    }

    @Override
    protected CorpRelatedEnterpriseListRSP lib2Rsp(CorpRelatedEnterpriseLib f) {
        CorpRelatedEnterpriseListRSP rsp = BeanUtil.copyProperties(f, CorpRelatedEnterpriseListRSP.class);
        rsp.setId(f.getOriginId());

        IndustryType industryType = industryTypeMapper.selectOne(Wrappers.<IndustryType>lambdaQuery()
                .eq(IndustryType::getCode, f.getIndustryType())
                .last("LIMIT 1"));
        rsp.setIndustryTypeName(Optional.ofNullable(industryType).map(IndustryType::getDisplay).orElse(null));
        return rsp;
    }

    @Override
    public InfoModule getSubModule() {
        return InfoModule.CORP_RELATED_ENTERPRISE;
    }

    @Override
    public List<CorpRelatedEnterprise> listNeedHandleEntity(Long mainId, Map<String, Object> extraMap) {
        return this.listNeedHandleEntity(mainId);
    }

}

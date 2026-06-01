package cn.zswltech.mithras.service.service.lib.client.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseListRSP;
import cn.zswltech.mithras.dto.client.relatedenterprise.NewCorpRelatedEnterpriseListRSP;
import cn.zswltech.mithras.service.enums.InfoModule;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.mapper.corp.IndustryTypeMapper;
import cn.zswltech.mithras.service.mapper.model.client.*;
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
public class NewCorpRelatedEnterpriseLibHandlerImpl extends ClientLibAbstractHandler<NewCorpRelatedEnterpriseLib, NewCorpRelatedEnterprise, NewCorpRelatedEnterpriseListRSP> {

    @Resource
    private IndustryTypeMapper industryTypeMapper;

    @Override
    protected NewCorpRelatedEnterpriseLib entity2Lib(NewCorpRelatedEnterprise newCorpRelatedEnterprise) {
        NewCorpRelatedEnterpriseLib newCorpRelatedEnterpriseLib = BeanUtil.copyProperties(newCorpRelatedEnterprise, NewCorpRelatedEnterpriseLib.class);
        return newCorpRelatedEnterpriseLib;
    }

    @Override
    protected NewCorpRelatedEnterprise lib2Entity(NewCorpRelatedEnterpriseLib newCorpRelatedEnterpriseLib) {
        NewCorpRelatedEnterprise newCorpRelatedEnterprise = BeanUtil.copyProperties(newCorpRelatedEnterpriseLib, NewCorpRelatedEnterprise.class);
        return newCorpRelatedEnterprise;
    }

    @Override
    public boolean needHandle(Long clientId, ClientType clientType) {
        return ClientType.CORPORATION.equals(clientType);
    }

    @Override
    protected NewCorpRelatedEnterpriseListRSP lib2Rsp(NewCorpRelatedEnterpriseLib f) {
        NewCorpRelatedEnterpriseListRSP rsp = BeanUtil.copyProperties(f, NewCorpRelatedEnterpriseListRSP.class);
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
    public List<NewCorpRelatedEnterprise> listNeedHandleEntity(Long mainId, Map<String, Object> extraMap) {
        return this.listNeedHandleEntity(mainId);
    }
}

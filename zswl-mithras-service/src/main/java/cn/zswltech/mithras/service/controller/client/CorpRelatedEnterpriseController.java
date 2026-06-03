package cn.zswltech.mithras.service.controller.client;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.client.CorpRelatedEnterpriseApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.relatedenterprise.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.client.ClientAddSubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientModifySubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientRemoveSubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientViewMainAuthCheckerNew;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpRelatedEnterpriseMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.IndustryTypeMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.*;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpRelatedEnterpriseService;
import cn.zswltech.mithras.customer.application.lib.client.CorpRelatedEnterpriseLibService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.hutool.core.util.ObjectUtil.isNotNull;

/**
 * @author luyi
 */
@RestController
public class CorpRelatedEnterpriseController implements CorpRelatedEnterpriseApi {
    @Resource
    private CorpRelatedEnterpriseService relatedEnterpriseService;
    @Resource
    private CorpRelatedEnterpriseLibService relatedEnterpriseLibService;
    @Resource
    private IndustryTypeMapper industryTypeMapper;

    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientAddSubAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT)
    public R<Void> add(CorpRelatedEnterpriseAddREQ req) {
        relatedEnterpriseService.add(req);
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientModifySubAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT, mapperClass =
//            CorpRelatedEnterpriseMapper.class)
    public R<Void> modify(CorpRelatedEnterpriseModifyREQ req) {
        relatedEnterpriseService.modify(req);
        return R.ok();
    }

    @Override
    @SneakyThrows
//    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientRemoveSubAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT, mapperClass = CorpRelatedEnterpriseMapper.class)
    public R<Void> remove(CorpRelatedEnterpriseRemoveREQ req) {
        if (isNotNull(req.getId())) {
            //根据id来删除
            relatedEnterpriseService.remove(req.getId());
        } else if (isNotBlank(req.getEnterpriseName()) && isNotNull(req.getClientId())) {
            relatedEnterpriseService.removeByName(req.getClientId(), req.getEnterpriseName());
        } else {
            throw new MissingServletRequestParameterException("id and enterpriseName", "");
        }
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientViewMainAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT)
    public R<PageR<CorpRelatedEnterpriseListRSP>> list(CorpRelatedEnterpriseListREQ req) {
//        if(!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), null)){
//            return R.ok(PageR.of(Collections.emptyList(), 0));
//        }
        //持股比例:shareholdingRatio,注册资本：registerCapital,投资金额：investAmount
        if (StringUtils.isBlank(req.getVersion())) {
            Page<CorpRelatedEnterprise> data = null;
            SFunction<NewCorpRelatedEnterprise, ?> orderBy = NewCorpRelatedEnterprise::getShareholdingRatio;
            if ("shareholdingRatio".equals(req.getOrderField())) {
                orderBy = NewCorpRelatedEnterprise::getShareholdingRatio;
            } else if ("registerCapital".equals(req.getOrderField())) {
                orderBy = NewCorpRelatedEnterprise::getRegisterCapital;
            } else if ("investAmount".equals(req.getOrderField())) {
                orderBy = NewCorpRelatedEnterprise::getInvestAmount;
            }
            data = relatedEnterpriseService.list(req, orderBy, req.getOrderType());
            List<CorpRelatedEnterpriseListRSP> list = BeanUtil.copyToList(data.getRecords(), CorpRelatedEnterpriseListRSP.class);
            Set<String> industryCodeList = new HashSet<>(list.size());
            //行业名称
            list.forEach(e -> industryCodeList.add(e.getIndustryType()));
            if (!industryCodeList.isEmpty()) {
                Map<String, String> industryMap = industryTypeMapper.selectList(Wrappers.<IndustryType>lambdaQuery().in(IndustryType::getCode, industryCodeList))
                        .stream().collect(Collectors.toMap(IndustryType::getCode, IndustryType::getDisplay));
                list.forEach(e -> {
                    e.setIndustryTypeName(industryMap.get(e.getIndustryType()));
                });
            }
            return R.ok(PageR.of(list, data.getTotal(),
                    data.getPages(),
                    data.getCurrent(),
                    data.getSize()));
        } else {
            SFunction<CorpRelatedEnterpriseLib, ?> orderBy = CorpRelatedEnterpriseLib::getShareholdingRatio;
            if ("shareholdingRatio".equals(req.getOrderField())) {
                orderBy = CorpRelatedEnterpriseLib::getShareholdingRatio;
            } else if ("registerCapital".equals(req.getOrderField())) {
                orderBy = CorpRelatedEnterpriseLib::getRegisterCapital;
            } else if ("investAmount".equals(req.getOrderField())) {
                orderBy = CorpRelatedEnterpriseLib::getInvestAmount;
            }
            return R.ok(relatedEnterpriseLibService.list(req, orderBy, req.getOrderType()));
        }

    }
}

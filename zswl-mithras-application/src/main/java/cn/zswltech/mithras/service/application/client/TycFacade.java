package cn.zswltech.mithras.service.application.client;

import cn.zswltech.mithras.customer.application.client.api.TycApplicationService;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.external.ExternalPageREQ;
import cn.zswltech.mithras.dto.client.external.ExternalSyncREQ;
import cn.zswltech.mithras.dto.client.external.tyc.*;
import cn.zswltech.mithras.customer.externaldata.tianyancha.infrastructure.mapper.*;
import cn.zswltech.mithras.customer.externaldata.tianyancha.infrastructure.model.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.client.ClientModifyMainAuthCheckerNew;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.third.tianyancha.application.convert.*;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.third.tianyancha.application.impl.TycExecutionService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 外部信息 天眼查
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:16 PM
 */
@Service
public class TycFacade implements TycApplicationService {

    @Resource
    private TycDishonestMapper tycDishonestMapper;
    @Resource
    private TycLawSuitMapper tycLawSuitMapper;
    @Resource
    private TycMortgageInfoMapper tycMortgageInfoMapper;
    @Resource
    private TycAbnormalMapper tycAbnormalMapper;
    @Resource
    private TycZhixingInfoMapper tycZhixingInfoMapper;
    @Resource
    private TycJudicialMapper tycJudicialMapper;
    @Resource
    private TycConsumptionRestrictionMapper tycConsumptionRestrictionMapper;
    @Resource
    private TycEquityInfoMapper tycEquityInfoMapper;
    @Resource
    private TycPunishmentInfoMapper tycPunishmentInfoMapper;
    @Resource
    private TycExecutionService tycExecutionService;
    @Resource
    private ClientMapper clientMapper;

    @Override
    public R<PageR<TycMortgageInfoRSP>> mortgageInfoList(ExternalPageREQ req) {
        if(!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), null)){
            return R.ok(PageR.of(Collections.emptyList(), 0));
        }
        Page<TycMortgageInfo> page = tycMortgageInfoMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<TycMortgageInfo>lambdaQuery().eq(TycMortgageInfo::getClientId, req.getClientId()));
        List<TycMortgageInfoRSP> rspList = page.getRecords().stream().map(TycMortgageInfoConvert::entity2RSP).collect(Collectors.toList());
        return R.ok(PageR.of(rspList, page.getTotal(),
                page.getPages(),
                page.getCurrent(),
                page.getSize()));
    }

    @Override
    public R<PageR<TycEquityInfoRSP>> equityInfo(ExternalPageREQ req) {
        if(!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), null)){
            return R.ok(PageR.of(Collections.emptyList(), 0));
        }
        Page<TycEquityInfo> page = tycEquityInfoMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<TycEquityInfo>lambdaQuery().eq(TycEquityInfo::getClientId, req.getClientId()));
        List<TycEquityInfoRSP> rspList = page.getRecords().stream().map(TycEquityInfoConvert::entity2RSP).collect(Collectors.toList());
        return R.ok(PageR.of(rspList, page.getTotal(),
                page.getPages(),
                page.getCurrent(),
                page.getSize()));
    }

    @Override
    public R<PageR<TycPunishmentInfoRSP>> punishmentInfoList(ExternalPageREQ req) {
        if(!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), null)){
            return R.ok(PageR.of(Collections.emptyList(), 0));
        }
        Page<TycPunishmentInfo> page = tycPunishmentInfoMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<TycPunishmentInfo>lambdaQuery().eq(TycPunishmentInfo::getClientId, req.getClientId()));
        List<TycPunishmentInfoRSP> rspList = page.getRecords().stream().map(TycPunishmentInfoConvert::entity2RSP).collect(Collectors.toList());
        return R.ok(PageR.of(rspList, page.getTotal(),
                page.getPages(),
                page.getCurrent(),
                page.getSize()));
    }

    @Override
    public R<PageR<TycAbnormalRSP>> abnormalList(ExternalPageREQ req) {
        if(!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), null)){
            return R.ok(PageR.of(Collections.emptyList(), 0));
        }
        Page<TycAbnormal> page = tycAbnormalMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<TycAbnormal>lambdaQuery().eq(TycAbnormal::getClientId, req.getClientId()));
        List<TycAbnormalRSP> rspList = page.getRecords().stream().map(TycAbnormalConvert::entity2RSP).collect(Collectors.toList());
        return R.ok(PageR.of(rspList, page.getTotal(),
                page.getPages(),
                page.getCurrent(),
                page.getSize()));
    }

    @Override
    public R<PageR<TycJudicialRSP>> judicialList(ExternalPageREQ req) {
        if(!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), null)){
           return R.ok(PageR.of(Collections.emptyList(), 0));
        }
        Page<TycJudicial> page = tycJudicialMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<TycJudicial>lambdaQuery().eq(TycJudicial::getClientId, req.getClientId()));
        List<TycJudicialRSP> rspList = page.getRecords().stream().map(TycJudicialConvert::entity2RSP).collect(Collectors.toList());
        return R.ok(PageR.of(rspList, page.getTotal(),
                page.getPages(),
                page.getCurrent(),
                page.getSize()));
    }

    @Override
    public R<PageR<TycLawSuitRSP>> lawSuitList(ExternalPageREQ req) {
        if(!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), null)){
            return  R.ok(PageR.of(Collections.emptyList(), 0));
        }
        Client client = clientMapper.selectById(req.getClientId());
        if (Objects.isNull(client)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        Page<TycLawSuit> page = tycLawSuitMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<TycLawSuit>lambdaQuery().eq(TycLawSuit::getClientId, req.getClientId()));
        List<TycLawSuitRSP> rspList = page.getRecords().stream().map(e -> TycLawSuitConvert.entity2RSP(e, client)).collect(Collectors.toList());
        return R.ok(PageR.of(rspList, page.getTotal(),
                page.getPages(),
                page.getCurrent(),
                page.getSize()));
    }

    @Override
    public R<PageR<TycConsumptionRestrictionRSP>> consumptionRestrictionList(ExternalPageREQ req) {
        if(!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), null)){
            return  R.ok(PageR.of(Collections.emptyList(), 0));
        }
        Page<TycConsumptionRestriction> page = tycConsumptionRestrictionMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<TycConsumptionRestriction>lambdaQuery().eq(TycConsumptionRestriction::getClientId, req.getClientId()));
        List<TycConsumptionRestrictionRSP> rspList = page.getRecords().stream().map(TycConsumptionRestrictionConvert::entity2RSP).collect(Collectors.toList());
        return R.ok(PageR.of(rspList, page.getTotal(),
                page.getPages(),
                page.getCurrent(),
                page.getSize()));
    }

    @Override
    public R<PageR<TycZhixingInfoRSP>> zhixingInfoList(ExternalPageREQ req) {
        if(!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), null)){
            return R.ok(PageR.of(Collections.emptyList(), 0));
        }
        Page<TycZhixingInfo> page = tycZhixingInfoMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<TycZhixingInfo>lambdaQuery().eq(TycZhixingInfo::getClientId, req.getClientId()));
        List<TycZhixingInfoRSP> rspList = page.getRecords().stream().map(TycZhixingInfoConvert::entity2RSP).collect(Collectors.toList());
        return R.ok(PageR.of(rspList, page.getTotal(),
                page.getPages(),
                page.getCurrent(),
                page.getSize()));
    }

    @Override
    public R<PageR<TycDishonestRSP>> dishonestList(ExternalPageREQ req) {
        if(!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), null)){
            return R.ok(PageR.of(Collections.emptyList(), 0));
        }
        Page<TycDishonest> page = tycDishonestMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<TycDishonest>lambdaQuery().eq(TycDishonest::getClientId, req.getClientId()));
        List<TycDishonestRSP> rspList = page.getRecords().stream().map(TycDishonestConvert::entity2RSP).collect(Collectors.toList());
        return R.ok(PageR.of(rspList, page.getTotal(),
                page.getPages(),
                page.getCurrent(),
                page.getSize()));
    }

    @Override
    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientModifyMainAuthCheckerNew.class, businessModule = "CLIENT")
    public R<Void> externalSync(ExternalSyncREQ req) {
        tycExecutionService.syncExternal(req.getClientId());
        return R.ok();
    }

}

package cn.zswltech.mithras.service.service.projestablish;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.projectprocess.enums.TradeStructureRoleEnum;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishTradeStructure;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishTradeStructureMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/7/18
 * @description
 */
@Slf4j
@Service
public class ProjEstablishTradeStructureService extends ServiceImpl<ProjEstablishTradeStructureMapper, ProjEstablishTradeStructure> {
    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;

    public List<Long> listClientIdsByProjEstablishId(Long projEstablishId) {
        LambdaQueryWrapper<ProjEstablishTradeStructure> query = Wrappers.lambdaQuery();
        query.eq(ProjEstablishTradeStructure::getProjEstablishId, projEstablishId);
        return this.list(query).stream().map(ProjEstablishTradeStructure::getClientId).distinct().collect(Collectors.toList());
    }

    public Set<Long> listProjEstablishIdsByClientId(Long clientId) {
        LambdaQueryWrapper<ProjEstablishTradeStructure> query = Wrappers.lambdaQuery();
        query.eq(ProjEstablishTradeStructure::getClientId, clientId);
        List<ProjEstablishTradeStructure> list = this.list(query);
        return list.stream().map(ProjEstablishTradeStructure::getProjEstablishId).collect(Collectors.toSet());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void syncTradeStructure(Long projEstablishId) {
        ProjEstablishBaseInfo projEstablishBaseInfo = projEstablishBaseInfoService.getById(projEstablishId);
        if (Objects.isNull(projEstablishBaseInfo)) {
            throw new MithrasException("立项主数据不存在");
        }
        // 移除老数据
        this.remove(Wrappers.<ProjEstablishTradeStructure>lambdaQuery().eq(ProjEstablishTradeStructure::getProjEstablishId, projEstablishId));
        // 保存交易结构客户
        List<ProjEstablishTradeStructure> toInsertList = new LinkedList<>();
        toInsertList.addAll(this.convert(projEstablishId, projEstablishBaseInfo.getLesseeInfo(), TradeStructureRoleEnum.LESSEE));
        toInsertList.addAll(this.convert(projEstablishId, projEstablishBaseInfo.getGuaranteeInfo(), TradeStructureRoleEnum.GUARANTOR));
        toInsertList.addAll(this.convert(projEstablishId, projEstablishBaseInfo.getMortgagorInfo(), TradeStructureRoleEnum.MORTGAGE));
        toInsertList.addAll(this.convert(projEstablishId, projEstablishBaseInfo.getPledgorInfo(), TradeStructureRoleEnum.PLEDGE));
        toInsertList.addAll(this.convert(projEstablishId, projEstablishBaseInfo.getCreditorInfo(), TradeStructureRoleEnum.CREDITOR));
        toInsertList.addAll(this.convert(projEstablishId, projEstablishBaseInfo.getDebtorInfo(), TradeStructureRoleEnum.DEBTOR));
        if (CollectionUtil.isNotEmpty(toInsertList)) {
            this.saveBatch(toInsertList);
        }
    }

    private List<ProjEstablishTradeStructure> convert(Long projEstablishId, String jsonArray, TradeStructureRoleEnum tradeStructureRoleEnum) {
        if (StrUtil.isBlank(jsonArray)) {
            return Collections.emptyList();
        }
        List<ProjEstablishPersonInfo> personInfoList = JSONUtil.toList(jsonArray, ProjEstablishPersonInfo.class);
        if (CollectionUtil.isEmpty(personInfoList)) {
            return Collections.emptyList();
        }
        personInfoList.removeIf(e -> Objects.isNull(e.getClientId()));
        return personInfoList.stream().map(e -> {
            ProjEstablishTradeStructure tradeStructure = new ProjEstablishTradeStructure();
            tradeStructure.setProjEstablishId(projEstablishId);
            tradeStructure.setClientId(e.getClientId());
            tradeStructure.setRole(tradeStructureRoleEnum.name());
            return tradeStructure;
        }).collect(Collectors.toList());
    }

    // 通过立项id查询角色为承租人/担保人/抵押人/质押人的客户ID
    public List<Long> listClientIdsByProjEstablishIds(List<Long> projEstablishIds) {
        LambdaQueryWrapper<ProjEstablishTradeStructure> query = Wrappers.lambdaQuery();
        query.in(ProjEstablishTradeStructure::getProjEstablishId, projEstablishIds);
        query.in(ProjEstablishTradeStructure::getRole, Arrays.asList(TradeStructureRoleEnum.LESSEE.name(),TradeStructureRoleEnum.GUARANTOR.name(),TradeStructureRoleEnum.MORTGAGE.name(),TradeStructureRoleEnum.PLEDGE.name()));
        query.eq(ProjEstablishTradeStructure::getDeleted,0);
        return this.list(query).stream().map(ProjEstablishTradeStructure::getClientId).distinct().collect(Collectors.toList());
    }
}

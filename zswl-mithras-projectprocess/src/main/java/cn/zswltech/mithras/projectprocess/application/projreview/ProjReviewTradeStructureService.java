package cn.zswltech.mithras.projectprocess.application.projreview;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.projectprocess.enums.TradeStructureRoleEnum;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewTradeStructure;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewTradeStructureMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/7/24
 * @description
 */
@Slf4j
@Service
public class ProjReviewTradeStructureService extends ServiceImpl<ProjReviewTradeStructureMapper, ProjReviewTradeStructure> {
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;

    public List<Long> listClientIdsByProjReviewId(Long projReviewId) {
        LambdaQueryWrapper<ProjReviewTradeStructure> query = Wrappers.lambdaQuery();
        query.eq(ProjReviewTradeStructure::getProjReviewId, projReviewId);
        return this.list(query).stream().map(ProjReviewTradeStructure::getClientId).distinct().collect(Collectors.toList());
    }

    public Set<Long> listProjReviewIdsByClientId(Long clientId) {
        LambdaQueryWrapper<ProjReviewTradeStructure> query = Wrappers.lambdaQuery();
        query.eq(ProjReviewTradeStructure::getClientId, clientId);
        List<ProjReviewTradeStructure> list = this.list(query);
        return list.stream().map(ProjReviewTradeStructure::getProjReviewId).collect(Collectors.toSet());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void syncTradeStructure(Long projReviewId) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(projReviewId);
        if (Objects.isNull(projReviewBaseInfo)) {
            throw new MithrasException("评审主数据不存在");
        }
        // 移除老数据
        this.remove(Wrappers.<ProjReviewTradeStructure>lambdaQuery().eq(ProjReviewTradeStructure::getProjReviewId, projReviewId));
        // 保存交易结构客户
        List<ProjReviewTradeStructure> toInsertList = new LinkedList<>();
        toInsertList.addAll(this.convert(projReviewId, projReviewBaseInfo.getLesseeInfo(), TradeStructureRoleEnum.LESSEE));
        toInsertList.addAll(this.convert(projReviewId, projReviewBaseInfo.getGuaranteeInfo(), TradeStructureRoleEnum.GUARANTOR));
        toInsertList.addAll(this.convert(projReviewId, projReviewBaseInfo.getMortgagorInfo(), TradeStructureRoleEnum.MORTGAGE));
        toInsertList.addAll(this.convert(projReviewId, projReviewBaseInfo.getPledgorInfo(), TradeStructureRoleEnum.PLEDGE));
        toInsertList.addAll(this.convert(projReviewId, projReviewBaseInfo.getCreditorInfo(), TradeStructureRoleEnum.CREDITOR));
        toInsertList.addAll(this.convert(projReviewId, projReviewBaseInfo.getDebtorInfo(), TradeStructureRoleEnum.DEBTOR));
        if (CollectionUtil.isNotEmpty(toInsertList)) {
            this.saveBatch(toInsertList);
        }
    }

    private List<ProjReviewTradeStructure> convert(Long projReviewId, String jsonArray, TradeStructureRoleEnum tradeStructureRoleEnum) {
        if (StrUtil.isBlank(jsonArray)) {
            return Collections.emptyList();
        }
        List<ClientInfo> personInfoList = JSONUtil.toList(jsonArray, ClientInfo.class);
        if (CollectionUtil.isEmpty(personInfoList)) {
            return Collections.emptyList();
        }
        personInfoList.removeIf(e -> Objects.isNull(e.getClientId()));
        return personInfoList.stream().map(e -> {
            ProjReviewTradeStructure tradeStructure = new ProjReviewTradeStructure();
            tradeStructure.setProjReviewId(projReviewId);
            tradeStructure.setClientId(e.getClientId());
            tradeStructure.setRole(tradeStructureRoleEnum.name());
            return tradeStructure;
        }).collect(Collectors.toList());
    }

    // 通过评审id 查询角色为承租人/担保人/抵押人/质押人的客户ID
    public List<Long> listClientIdsByProjReviewIds(List<Long> projReviewIds) {
        LambdaQueryWrapper<ProjReviewTradeStructure> query = Wrappers.lambdaQuery();
        query.in(ProjReviewTradeStructure::getProjReviewId, projReviewIds);
        query.in(ProjReviewTradeStructure::getRole,Arrays.asList(TradeStructureRoleEnum.LESSEE.name(),TradeStructureRoleEnum.GUARANTOR.name(),TradeStructureRoleEnum.MORTGAGE.name(),TradeStructureRoleEnum.PLEDGE.name()));
        query.eq(ProjReviewTradeStructure::getDeleted,0);
        return this.list(query).stream().map(ProjReviewTradeStructure::getClientId).distinct().collect(Collectors.toList());
    }
}

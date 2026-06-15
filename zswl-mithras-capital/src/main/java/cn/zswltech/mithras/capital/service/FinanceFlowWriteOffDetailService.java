package cn.zswltech.mithras.capital.service;

import cn.zswltech.mithras.capital.persistence.mapper.writeoff.FinanceFlowWriteOffDetailMapper;
import cn.zswltech.mithras.capital.persistence.model.writeoff.FinanceFlowWriteOffDetail;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FinanceFlowWriteOffDetailService {

    @Resource
    private FinanceFlowWriteOffDetailMapper financeFlowWriteOffDetailMapper;

    public void create(String recordMainTable, Long mainId, String bankDetailNo, Long financeFlowId) {
        create(recordMainTable, mainId, bankDetailNo, financeFlowId, null, null);
    }

    public void create(String recordMainTable, Long mainId, String bankDetailNo, Long financeFlowId, Long createBy, Long updateBy) {
        FinanceFlowWriteOffDetail detail = new FinanceFlowWriteOffDetail();
        detail.setRecordMainTable(recordMainTable);
        detail.setMainId(mainId);
        detail.setBankDetailNo(bankDetailNo);
        detail.setFinanceFlowId(financeFlowId);
        detail.setCreateBy(createBy);
        detail.setUpdateBy(updateBy);
        financeFlowWriteOffDetailMapper.insert(detail);
    }

    public List<FinanceFlowWriteOffDetail> listByFinanceFlowIdAndMainTable(Long financeFlowId, String recordMainTable) {
        return financeFlowWriteOffDetailMapper.selectList(Wrappers.<FinanceFlowWriteOffDetail>lambdaQuery()
                .eq(FinanceFlowWriteOffDetail::getFinanceFlowId, financeFlowId)
                .eq(FinanceFlowWriteOffDetail::getRecordMainTable, recordMainTable));
    }

    public List<Long> listMainIdsByFinanceFlowIdAndMainTable(Long financeFlowId, String recordMainTable) {
        List<FinanceFlowWriteOffDetail> details = listByFinanceFlowIdAndMainTable(financeFlowId, recordMainTable);
        if (details == null || details.isEmpty()) {
            return Collections.emptyList();
        }
        return details.stream()
                .map(FinanceFlowWriteOffDetail::getMainId)
                .collect(Collectors.toList());
    }

    public List<FinanceFlowWriteOffDetail> listByFinanceFlowIds(Collection<Long> financeFlowIds) {
        if (financeFlowIds == null || financeFlowIds.isEmpty()) {
            return Collections.emptyList();
        }
        return financeFlowWriteOffDetailMapper.selectList(Wrappers.<FinanceFlowWriteOffDetail>lambdaQuery()
                .in(FinanceFlowWriteOffDetail::getFinanceFlowId, financeFlowIds));
    }

    public Map<String, List<Long>> groupMainIdsByMainTable(Collection<Long> financeFlowIds) {
        return listByFinanceFlowIds(financeFlowIds).stream()
                .collect(Collectors.groupingBy(
                        FinanceFlowWriteOffDetail::getRecordMainTable,
                        Collectors.mapping(FinanceFlowWriteOffDetail::getMainId, Collectors.toList())));
    }

    public Long logicalDeleteFirstByMainTableAndMainId(String recordMainTable, Long mainId) {
        FinanceFlowWriteOffDetail detail = financeFlowWriteOffDetailMapper.selectOne(Wrappers.<FinanceFlowWriteOffDetail>lambdaQuery()
                .eq(FinanceFlowWriteOffDetail::getRecordMainTable, recordMainTable)
                .eq(FinanceFlowWriteOffDetail::getMainId, mainId)
                .last(StringUtil.mysqlLimitOne()));
        if (detail == null) {
            return null;
        }
        LambdaUpdateWrapper<FinanceFlowWriteOffDetail> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(FinanceFlowWriteOffDetail::getDeleted, YesOrNoNumberEnum.YES.getCode());
        updateWrapper.eq(FinanceFlowWriteOffDetail::getId, detail.getId());
        financeFlowWriteOffDetailMapper.update(null, updateWrapper);
        return detail.getFinanceFlowId();
    }
}

package cn.zswltech.mithras.service.service.finance;

import cn.zswltech.mithras.service.mapper.finance.FinanceOverdueVersionRelationMapper;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceOverdueVersionRelation;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
* @description 应收逾期版本记录表
* @author vico
* @date 2025-09-15
*/
@Service
public class FinanceOverdueVersionRelationService extends ServiceImpl<FinanceOverdueVersionRelationMapper, FinanceOverdueVersionRelation> {

    public List<FinanceOverdueVersionRelation> listByProcessInstanceId(String processInstanceId, String recordType) {
        List<FinanceOverdueVersionRelation> list = this.list(Wrappers.<FinanceOverdueVersionRelation>lambdaQuery()
                .eq(FinanceOverdueVersionRelation::getProcessInstanceId, processInstanceId)
                .eq(FinanceOverdueVersionRelation::getRecordType, recordType));
        if (!list.isEmpty()) {
            return list;
        }
        if(this.count(Wrappers.<FinanceOverdueVersionRelation>lambdaQuery()
                .eq(FinanceOverdueVersionRelation::getProcessInstanceId, processInstanceId)) > 0) {
            FinanceOverdueVersionRelation financeOverdueVersionRelation = new FinanceOverdueVersionRelation();
            financeOverdueVersionRelation.setRecordId(-9999L);
            return Collections.singletonList(financeOverdueVersionRelation);
        } else {
            return new ArrayList<>();
        }
    }
}

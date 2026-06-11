package cn.zswltech.mithras.kpi.application.projguess;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.kpi.mapper.KpiProjGuessDivideMapper;
import cn.zswltech.mithras.kpi.model.KpiProjGuessDivide;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @description 绩效-项目测算分配表
* @author vico
* @date 2023-06-16
*/
@Service
public class KpiProjGuessDivideService extends ServiceImpl<KpiProjGuessDivideMapper, KpiProjGuessDivide> {
    public List<KpiProjGuessDivide> listByContractYearMonth(Long contractId, int year, int month) {
        LambdaQueryWrapper<KpiProjGuessDivide> query = Wrappers.lambdaQuery();
        query.eq(KpiProjGuessDivide::getContractId, contractId);
        query.eq(KpiProjGuessDivide::getDivideYear, year);
        query.eq(KpiProjGuessDivide::getDivideMonth, month);
        return this.list(query);
    }

    public List<KpiProjGuessDivide> listByContractIdsYearMonth(List<Long> contractIds, int year, int month) {
        LambdaQueryWrapper<KpiProjGuessDivide> query = Wrappers.lambdaQuery();
        query.in(ObjectUtil.isNotEmpty(contractIds), KpiProjGuessDivide::getContractId, contractIds);
        query.eq(KpiProjGuessDivide::getDivideYear, year);
        query.eq(KpiProjGuessDivide::getDivideMonth, month);
        return this.list(query);
    }
}
package cn.zswltech.mithras.service.service.monthly;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.monthly.*;
import cn.zswltech.mithras.service.enums.OverdueTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.contract.IncomeConfirmTypeEnum;
import cn.zswltech.mithras.service.enums.monthly.MonthlyModuleTypeEnum;
import cn.zswltech.mithras.service.mapper.contract.ContractIncomeSharingMapper;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.monthly.MonthlyManageBaseModel;
import cn.zswltech.mithras.service.mapper.model.monthly.MonthlyManagementAirRecord;
import cn.zswltech.mithras.service.mapper.model.monthly.MonthlyManagementBaseInfo;
import cn.zswltech.mithras.service.mapper.monthly.MonthlyManagementAirRecordMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.kpi.service.KpiParameterConfigService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @description 针对表【monthly_management_air_record(实际利率法-记录表)】的数据库操作Service实现
 * @createDate 2024-07-24 15:12:50
 */
@Service
public class MonthlyManagementAirRecordService extends ServiceImpl<MonthlyManagementAirRecordMapper, MonthlyManagementAirRecord>
        implements IService<MonthlyManagementAirRecord> {

    @Resource
    private MonthlyManagementAirRecordService airRecordService;
    @Resource
    private MonthlyManagementBaseInfoService baseInfoService;
    @Resource
    private ContractIncomeSharingMapper contractIncomeSharingMapper;
    @Resource
    private KpiParameterConfigService kpiParameterConfigService;

    @Transactional(rollbackFor = Throwable.class)
    public void freshMonthlyAirRecord(MonthlyFreshREQ req) {
        MonthlyManagementBaseInfo baseInfo = baseInfoService.getMonthlyManagementBaseInfo(req.getYearAndMonth());
        LocalDate endDate = LocalDate.of(baseInfo.getYear(), baseInfo.getMonth(), 1).with(TemporalAdjusters.lastDayOfMonth());
        Map<Long, ContractBaseInfo> contract = baseInfoService.getContract(IncomeConfirmTypeEnum.AIR, endDate, false);
        MonthlyQuery monthlyQuery = new MonthlyQuery();
        monthlyQuery.setEndMonth(endDate.with(TemporalAdjusters.lastDayOfMonth()));
        monthlyQuery.setBeginMonth(endDate.with(TemporalAdjusters.firstDayOfMonth()));
        monthlyQuery.setContractIdList(contract.keySet());
        Page<MonthlyQueryResult> page = new Page<>(1, Integer.MAX_VALUE);
        Page<MonthlyQueryResult> list = contractIncomeSharingMapper.queryMonthlyData(page, monthlyQuery);
        //找到已经存在的记录
        List<MonthlyManagementAirRecord> existedList = airRecordService.list(Wrappers.<MonthlyManagementAirRecord>lambdaQuery()
                .eq(MonthlyManageBaseModel::getMainId, baseInfo.getMainId()));
        Map<Long, MonthlyManagementAirRecord> longMap = new HashMap<>();
        List<Long> longList = new ArrayList<>(16);
        if (!existedList.isEmpty()) {
            longMap = existedList.stream().collect(Collectors.toMap(MonthlyManagementAirRecord::getReceiptId, Function.identity(), (v1, v2) -> v1));
            longList = existedList.stream().map(MonthlyManagementAirRecord::getReceiptId).collect(Collectors.toList());
        }
        List<Long> existedIdList = longList;
        Map<Long, MonthlyManagementAirRecord> existedMap = longMap;
        List<MonthlyManagementAirRecord> needInsertList = new ArrayList<>(16);
        List<MonthlyManagementAirRecord> needUpdateList = new ArrayList<>(16);
        list.getRecords().forEach(dto -> {
            MonthlyManagementAirRecord airRecord = existedMap.get(dto.getReceiptId());
            if (existedIdList.contains(dto.getReceiptId())) {
                boolean isEqual = true;
                if (!dto.getIncomeSum().equals(airRecord.getIncomeSum())) {
                    isEqual = false;
                }
                if (!dto.getIncomeWithoutTaxSum().equals(airRecord.getIncomeWithoutTaxSum())) {
                    isEqual = false;
                }
                if (!isEqual) {
                    // 根据状态是否确认，判断是否需要更新
                    boolean needChange = YesOrNoNumberEnum.YES.getCode().equals(airRecord.getIsConfirmed());
                    if (needChange) {
                        //已经确认的数据，变化了，需要再查看更新类型
                        return;
                    }
                }
            }
            MonthlyManagementAirRecord rsp = new MonthlyManagementAirRecord();
            BeanUtil.copyProperties(dto, rsp, "id", "createBy", "createTime", "updateBy", "updateTime", "deleted", "actualLeaseDate");
            rsp.setActualLeaseDate(dto.getActualLeaseDate().atStartOfDay());
            BigDecimal bigDecimal = kpiParameterConfigService.ensureXMSRate(dto.getBizType(), dto.getLeaseType());
            rsp.setTaxRate(Util.toMithrasUnit(bigDecimal.multiply(BigDecimal.valueOf(100))));
            // 以将逾期的数据过滤
            rsp.setOverdueType(OverdueTypeEnum.NOT_OVERDUE.name());
            rsp.setClientId(dto.getClientId());
            rsp.setSourceId(dto.getReceiptId());
            rsp.setReceiptId(dto.getReceiptId());
            rsp.setMainId(baseInfo.getMainId());
            rsp.setReceiptCode(dto.getReceiptCode());
            rsp.setNewUpdate(YesOrNoNumberEnum.NO.getCode());
            if (existedIdList.contains(dto.getReceiptId())) {
                rsp.setId(airRecord.getId());
                needUpdateList.add(rsp);
            } else {
                needInsertList.add(rsp);
            }
        });
        if (!needInsertList.isEmpty()) {
            airRecordService.saveBatch(needInsertList);
        }
        if (!needUpdateList.isEmpty()) {
            airRecordService.updateBatchById(needUpdateList);
        }
    }

    public PageR<MonthlyAIRListRSP> airList(MonthlyAIRListREQ req) {
        MonthlyManagementBaseInfo baseInfo = baseInfoService.getMonthlyManagementBaseInfo(req.getYearAndMonth());
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(String.format("%s的月结主数据不存在, 请先创建月结主信息", req.getYearAndMonth()));
        }
        //查询实际利率法记录
        Page<MonthlyManagementAirRecord> page = new Page<>(req.getPage(), req.getPageSize());
        Page<MonthlyManagementAirRecord> list = airRecordService.page(page, Wrappers.<MonthlyManagementAirRecord>lambdaQuery()
                .eq(Objects.nonNull(req.getClientId()), MonthlyManagementAirRecord::getClientId, req.getClientId())
                .like(CharSequenceUtil.isNotBlank(req.getContractCode()), MonthlyManagementAirRecord::getContractCode, req.getContractCode())
                .eq(MonthlyManagementAirRecord::getMainId, baseInfo.getMainId())
                .eq(CharSequenceUtil.isNotBlank(req.getOverdueType()), MonthlyManagementAirRecord::getOverdueType, req.getOverdueType())
                .eq(CharSequenceUtil.isNotBlank(req.getBatchNumber()), MonthlyManageBaseModel::getBatchNumber, req.getBatchNumber()));

        if (list.getRecords().isEmpty()) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }

        List<MonthlyAIRListRSP> rspList = list.getRecords().stream().map(dto -> {
            MonthlyAIRListRSP rsp = new MonthlyAIRListRSP();
            rsp.setYearAndMonth(String.format("%d-%02d", baseInfo.getYear(), baseInfo.getMonth()));
            BeanUtil.copyProperties(dto, rsp, "actualLeaseDate");
            rsp.setActualLeaseDate(dto.getActualLeaseDate().toLocalDate());
            rsp.setTabType(MonthlyModuleTypeEnum.AIR.name());
            rsp.setNewUpdated(dto.getNewUpdate());
            return rsp;
        }).collect(Collectors.toList());
        return PageR.of(rspList, list.getTotal(), list.getCurrent(), list.getSize());
    }
}





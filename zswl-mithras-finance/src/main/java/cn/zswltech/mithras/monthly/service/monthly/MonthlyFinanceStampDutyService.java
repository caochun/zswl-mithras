package cn.zswltech.mithras.monthly.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.monthly.MonthlyFreshREQ;
import cn.zswltech.mithras.dto.monthly.MonthlyStampDutyFinREQ;
import cn.zswltech.mithras.dto.monthly.MonthlyStampDutyFinRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.monthly.enums.MonthlyModuleTypeEnum;
import cn.zswltech.mithras.monthly.enums.StampDutyTypeEnum;
import cn.zswltech.mithras.monthly.mapper.model.MonthlyFinanceStampDuty;
import cn.zswltech.mithras.monthly.mapper.model.MonthlyManageBaseModel;
import cn.zswltech.mithras.monthly.mapper.model.MonthlyManagementBaseInfo;
import cn.zswltech.mithras.monthly.mapper.model.MonthlyStampDuty;
import cn.zswltech.mithras.monthly.mapper.MonthlyFinanceStampDutyMapper;
import cn.zswltech.mithras.monthly.mapper.MonthlyManagementBaseInfoMapper;
import cn.zswltech.mithras.monthly.mapper.MonthlyStampDutyMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @description 针对表【monthly_finance_stamp_duty(月结管理资金端印花税)】的数据库操作Service实现
 * @createDate 2024-07-30 17:30:20
 */
@Service
public class MonthlyFinanceStampDutyService extends ServiceImpl<MonthlyFinanceStampDutyMapper, MonthlyFinanceStampDuty> {

    @Resource
    private MonthlyFinanceStampDutyService monthlyFinanceStampDutyService;
    @Resource
    private MonthlyManagementBaseInfoMapper baseInfoMapper;
    @Resource
    private MonthlyStampDutyMapper stampDutyMapper;

    public PageR<MonthlyStampDutyFinRSP> finPage(MonthlyStampDutyFinREQ req) {
        // 融资：业务类型=项目贷款/流动资金贷款
        MonthlyManagementBaseInfo baseInfo = getMonthlyManagementBaseInfo(req.getYearAndMonth());
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(String.format("%s的月结主数据不存在, 请先创建月结主信息", req.getYearAndMonth()));
        }

        Page<MonthlyFinanceStampDuty> page = new Page<>(req.getPage(), req.getPageSize());
        Page<MonthlyFinanceStampDuty> pageRecord = monthlyFinanceStampDutyService.page(page, Wrappers.<MonthlyFinanceStampDuty>lambdaQuery()
                .like(StringUtils.hasText(req.getFinancingCode()), MonthlyFinanceStampDuty::getFinancingCode, req.getFinancingCode())
                .like(StringUtils.hasText(req.getOrganizationName()), MonthlyFinanceStampDuty::getOrganizationName, req.getOrganizationName())
                .eq(MonthlyFinanceStampDuty::getMainId, baseInfo.getMainId())
                .eq(StringUtils.hasText(req.getBatchNumber()), MonthlyManageBaseModel::getBatchNumber, req.getBatchNumber()));
        List<MonthlyFinanceStampDuty> monthlyStampDutyList = pageRecord.getRecords();
        List<MonthlyStampDutyFinRSP> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(monthlyStampDutyList)) {
            monthlyStampDutyList.forEach(monthlyStampDuty -> {
                MonthlyStampDutyFinRSP copied = new MonthlyStampDutyFinRSP();
                BeanUtils.copyProperties(monthlyStampDuty, copied);
                copied.setTabType(MonthlyModuleTypeEnum.STAMP_DUTY_FIN.name());
                copied.setYearAndMonth(String.format("%d-%02d", baseInfo.getYear(), baseInfo.getMonth()));
                copied.setNewUpdated(monthlyStampDuty.getNewUpdate());
                result.add(copied);
            });
        }
        return PageR.of(result, page.getTotal());
    }

    public void freshFinanceStampDuty(MonthlyFreshREQ req) {
        MonthlyManagementBaseInfo baseInfo = getMonthlyManagementBaseInfo(req.getYearAndMonth());
        LocalDate currentDate = handleDate(req.getYearAndMonth());
        LocalDate startMonth = currentDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endMonth = currentDate.with(TemporalAdjusters.lastDayOfMonth());
        Page<MonthlyStampDuty> page = new Page<>(1, Integer.MAX_VALUE);
        List<MonthlyStampDuty> monthlyStampDutyList = stampDutyMapper.selectPage(page, Wrappers.<MonthlyStampDuty>lambdaQuery()
                        .eq(Objects.nonNull(req.getSourceId()), MonthlyStampDuty::getId, req.getSourceId())
                        .in(MonthlyStampDuty::getType, Arrays.asList(StampDutyTypeEnum.FIN_DIRECT_FIN.name(), StampDutyTypeEnum.FIN_FIN.name()))
                        .ge(MonthlyStampDuty::getDate, startMonth)
                        .le(MonthlyStampDuty::getDate, endMonth))
                .getRecords();
        // 找到已经存在的数据
        List<MonthlyFinanceStampDuty> existFianceList = monthlyFinanceStampDutyService.list(Wrappers.<MonthlyFinanceStampDuty>lambdaQuery()
                .eq(MonthlyFinanceStampDuty::getMainId, baseInfo.getMainId()));
        List<Long> longList = new ArrayList<>();
        Map<Long, MonthlyFinanceStampDuty> longMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(existFianceList)) {
            longList = existFianceList.stream().map(MonthlyFinanceStampDuty::getSourceId).collect(Collectors.toList());
            longMap = existFianceList.stream().collect(Collectors.toMap(MonthlyFinanceStampDuty::getSourceId, Function.identity(), (v1, v2) -> v1));
        }
        List<Long> existedIdList = longList;
        Map<Long, MonthlyFinanceStampDuty> existedMap = longMap;
        List<MonthlyFinanceStampDuty> needInsertList = new ArrayList<>(16);
        List<MonthlyFinanceStampDuty> needUpdateList = new ArrayList<>(16);
        if (CollectionUtils.isNotEmpty(monthlyStampDutyList)) {
            monthlyStampDutyList.stream().forEach(dto -> {
                if (existedIdList.contains(dto.getId())) {
                    boolean isEqual = true;
                    if (!dto.getStampDuty().equals(existedMap.get(dto.getId()).getStampDuty())) {
                        isEqual = false;
                    }
                    if (!isEqual) {
                        // 根据状态是否确认，判断是否需要更新
                        boolean needChange = YesOrNoNumberEnum.YES.getCode().equals(existedMap.get(dto.getId()).getIsConfirmed());
                        if (needChange) {
                            //已经确认的数据，变化了，需要再查看更新类型
                            return;
                        }
                    }
                }
                MonthlyFinanceStampDuty financeStampDuty = new MonthlyFinanceStampDuty();
                BeanUtils.copyProperties(dto, financeStampDuty, GlobalConstants.COPY_IGNORE_COMMON_FIELD);
                financeStampDuty.setMainId(baseInfo.getMainId());
                financeStampDuty.setSourceId(dto.getId());
                financeStampDuty.setNewUpdate(YesOrNoNumberEnum.NO.getCode());
                if (existedIdList.contains(dto.getId())) {
                    financeStampDuty.setSourceId(existedMap.get(dto.getId()).getId());
                    financeStampDuty.setId(existedMap.get(dto.getId()).getId());
                    needUpdateList.add(financeStampDuty);
                } else {
                    needInsertList.add(financeStampDuty);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(needInsertList)) {
            monthlyFinanceStampDutyService.saveBatch(needInsertList);
        }
        if (CollectionUtils.isNotEmpty(needUpdateList)) {
            monthlyFinanceStampDutyService.updateBatchById(needUpdateList);
        }
    }

    private LocalDate handleDate(String yearAndMonth) {
        try {
            return LocalDate.parse(yearAndMonth + "-01", DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new MithrasException("无法识别的时间格式");
        }
    }

    private MonthlyManagementBaseInfo getMonthlyManagementBaseInfo(String yearAndMonth) {
        if (!StringUtils.hasText(yearAndMonth)) {
            throw new MithrasException("日期不能为空");
        }
        LocalDate localDate = handleDate(yearAndMonth);
        return baseInfoMapper.selectOne(Wrappers.<MonthlyManagementBaseInfo>lambdaQuery()
                .eq(MonthlyManagementBaseInfo::getYear, localDate.getYear())
                .eq(MonthlyManagementBaseInfo::getMonth, localDate.getMonthValue()));
    }

}





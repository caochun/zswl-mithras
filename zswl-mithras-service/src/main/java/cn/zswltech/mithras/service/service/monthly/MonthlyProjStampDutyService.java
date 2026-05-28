package cn.zswltech.mithras.service.service.monthly;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.monthly.MonthlyFreshREQ;
import cn.zswltech.mithras.dto.monthly.MonthlyStampDutyProjREQ;
import cn.zswltech.mithras.dto.monthly.MonthlyStampDutyProjRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.monthly.MonthlyModuleTypeEnum;
import cn.zswltech.mithras.service.enums.monthly.StampDutyTypeEnum;
import cn.zswltech.mithras.service.mapper.model.monthly.MonthlyManageBaseModel;
import cn.zswltech.mithras.service.mapper.model.monthly.MonthlyManagementBaseInfo;
import cn.zswltech.mithras.service.mapper.model.monthly.MonthlyProjStampDuty;
import cn.zswltech.mithras.service.mapper.model.monthly.MonthlyStampDuty;
import cn.zswltech.mithras.service.mapper.monthly.MonthlyProjStampDutyMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @description 针对表【monthly_proj_stamp_duty(月结管理项目端印花税)】的数据库操作Service实现
 * @createDate 2024-07-30 17:30:20
 */
@Service
public class MonthlyProjStampDutyService extends ServiceImpl<MonthlyProjStampDutyMapper, MonthlyProjStampDuty> {

    @Resource
    private MonthlyManagementBaseInfoService baseInfoService;
    @Resource
    private MonthlyProjStampDutyService projStampDutyService;
    @Resource
    private MonthlyStampDutyService monthlyStampDutyService;

    @Transactional(rollbackFor = Throwable.class)
    public void freshProjStampDuty(MonthlyFreshREQ freshReq) {
        MonthlyManagementBaseInfo baseInfo = baseInfoService.getMonthlyManagementBaseInfo(freshReq.getYearAndMonth());
        LocalDate currentMonth = LocalDate.of(baseInfo.getYear(), baseInfo.getMonth(), 1);
        LocalDate endMonth = currentMonth.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate startMonth = currentMonth.with(TemporalAdjusters.firstDayOfMonth());
        List<MonthlyStampDuty> monthlyStampDutyList = monthlyStampDutyService.list(Wrappers.<MonthlyStampDuty>lambdaQuery()
                .eq(Objects.nonNull(freshReq.getSourceId()), MonthlyStampDuty::getId, freshReq.getSourceId())
                .in(MonthlyStampDuty::getType, StampDutyTypeEnum.PROJ.name())
                .ge(MonthlyStampDuty::getDate, startMonth)
                .le(MonthlyStampDuty::getDate, endMonth));

        // 找到现有数据
        List<MonthlyProjStampDuty> existProjStampDutyList = projStampDutyService.list(Wrappers.<MonthlyProjStampDuty>lambdaQuery()
                .eq(MonthlyProjStampDuty::getMainId, baseInfo.getMainId()));
        List<Long> longList = new ArrayList<>();
        Map<Long, MonthlyProjStampDuty> longMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(existProjStampDutyList)) {
            longList = existProjStampDutyList.stream().map(MonthlyProjStampDuty::getSourceId).collect(Collectors.toList());
            longMap = existProjStampDutyList.stream().collect(Collectors.toMap(MonthlyProjStampDuty::getSourceId, Function.identity(), (v1, v2) -> v1));
        }
        List<Long> existedIdList = longList;
        Map<Long, MonthlyProjStampDuty> existedMap = longMap;
        List<MonthlyProjStampDuty> needInsertList = new ArrayList<>(16);
        List<MonthlyProjStampDuty> needUpdateList = new ArrayList<>(16);

        if (CollectionUtils.isNotEmpty(monthlyStampDutyList)) {
            monthlyStampDutyList.forEach(monthlyStampDuty -> {
                if (existedIdList.contains(monthlyStampDuty.getId())) {
                    boolean isEqual = monthlyStampDuty.getStampDuty().equals(existedMap.get(monthlyStampDuty.getId()).getStampDuty());
                    if (!isEqual) {
                        // 根据状态是否确认，判断是否需要更新
                        boolean needChange = YesOrNoNumberEnum.YES.getCode().equals(existedMap.get(monthlyStampDuty.getId()).getIsConfirmed());
                        if (needChange) {
                            //已经确认的数据，变化了，需要再查看更新类型
                            return;
                        }
                    }
                }
                MonthlyProjStampDuty projStampDuty = BeanUtil.copyProperties(monthlyStampDuty, MonthlyProjStampDuty.class, GlobalConstants.COPY_IGNORE_COMMON_FIELD);
                projStampDuty.setMainId(baseInfo.getMainId());
                projStampDuty.setSourceId(monthlyStampDuty.getId());
                projStampDuty.setNewUpdate(YesOrNoNumberEnum.NO.getCode());
                if (existedIdList.contains(monthlyStampDuty.getId())) {
                    projStampDuty.setId(existedMap.get(monthlyStampDuty.getId()).getId());
                    needUpdateList.add(projStampDuty);
                } else {
                    needInsertList.add(projStampDuty);
                }
            });
            if (!CollectionUtils.isEmpty(needInsertList)) {
                projStampDutyService.saveBatch(needInsertList);
            }
            if (!CollectionUtils.isEmpty(needUpdateList)) {
                projStampDutyService.updateBatchById(needUpdateList);
            }
        }
    }

    public PageR<MonthlyStampDutyProjRSP> projPage(MonthlyStampDutyProjREQ req) {
        MonthlyManagementBaseInfo baseInfo = baseInfoService.getMonthlyManagementBaseInfo(req.getYearAndMonth());
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException("月结管理" + ResultMsg.RECORD_NOT_EXIST);
        }

        Page<MonthlyProjStampDuty> page = new Page<>(req.getPage(), req.getPageSize());
        Page<MonthlyProjStampDuty> dutyPage = projStampDutyService.page(page, Wrappers.<MonthlyProjStampDuty>lambdaQuery()
                .eq(Objects.nonNull(req.getClientId()), MonthlyProjStampDuty::getClientId, req.getClientId())
                .like(CharSequenceUtil.isNotBlank(req.getContractCode()), MonthlyProjStampDuty::getContractCode, req.getContractCode())
                .eq(MonthlyProjStampDuty::getMainId, baseInfo.getMainId())
                .eq(CharSequenceUtil.isNotBlank(req.getBatchNumber()), MonthlyManageBaseModel::getBatchNumber, req.getBatchNumber()));
        if (CollUtil.isEmpty(dutyPage.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<MonthlyStampDutyProjRSP> result = new ArrayList<>();
        dutyPage.getRecords().forEach(rsp -> {
            MonthlyStampDutyProjRSP projRsp = new MonthlyStampDutyProjRSP();
            BeanUtil.copyProperties(rsp, projRsp);
            projRsp.setYearAndMonth(String.format("%d-%02d", baseInfo.getYear(), baseInfo.getMonth()));
            projRsp.setTabType(MonthlyModuleTypeEnum.STAMP_DUTY_PROJ.name());
            projRsp.setNewUpdated(rsp.getNewUpdate());
            result.add(projRsp);
        });
        return PageR.of(result, dutyPage.getTotal());
    }
}





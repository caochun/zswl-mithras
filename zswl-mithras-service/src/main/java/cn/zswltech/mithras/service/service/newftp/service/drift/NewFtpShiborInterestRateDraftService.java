package cn.zswltech.mithras.service.service.newftp.service.drift;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.spring.SpringContextUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.newftp.NewFtpCommonDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailTreasuryBondYieldListRSP;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.ftp.enums.FtpFrequency;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.ftp.newftp.mapper.NewFtpBaseInfoMapper;
import cn.zswltech.mithras.ftp.newftp.mapper.config.NewFtpShiborInterestRateConfigMapper;
import cn.zswltech.mithras.ftp.newftp.mapper.draft.NewFtpShiborInterestRateDraftMapper;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpShiborInterestRateConfig;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpShiborInterestRateDraft;
import cn.zswltech.mithras.ftp.newftp.service.drift.NewFtpShiborInterestRatePricingDraftService;
import cn.zswltech.mithras.ftp.newftp.service.lib.NewFtpShiborInterestRateLibService;
import cn.zswltech.mithras.service.util.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * @description 1年期SHIBOR利率
 * @date 2023-05-21
 */
@Service
public class NewFtpShiborInterestRateDraftService
        extends ServiceImpl<NewFtpShiborInterestRateDraftMapper, NewFtpShiborInterestRateDraft> {
    @Resource
    private NewFtpShiborInterestRateConfigMapper newFtpShiborInterestRateConfigMapper;
    @Resource
    private NewFtpBaseInfoMapper newFtpBaseInfoMapper;
    @Resource
    private NewFtpShiborInterestRatePricingDraftService newFtpShiborInterestRatePricingDraftService;
    @Resource
    private NewFtpShiborInterestRateLibService newFtpShiborInterestRateLibService;

    /**
     * 查询1年期SHIBOR利率
     **/
    public PageR<NewFtpDetailTreasuryBondYieldListRSP> shiborInterestList(NewFtpCommonDetailReq req) {
        FtpFrequency ftpFrequency = ofNullable(FtpFrequency.of(req.getFrequency())).orElseThrow(() -> new MithrasException("暂不支持"));
        Page<NewFtpShiborInterestRateDraft> page;
        if(ObjectUtil.isEmpty(req.getVersion())){
            page = this.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<NewFtpShiborInterestRateDraft>lambdaQuery()
                    .eq(NewFtpShiborInterestRateDraft::getFtpId, req.getMainId())
                    .eq(NewFtpShiborInterestRateDraft::getFrequency, req.getFrequency())
                    .orderByDesc(NewFtpShiborInterestRateDraft::getDate));
        } else {
            page = newFtpShiborInterestRateLibService.getByVersion(req.getMainId(), req.getFrequency(), req.getVersion(), req.getPage(), req.getPageSize());
        }
        if (ObjectUtil.isEmpty(page) && ObjectUtil.isNotEmpty(page.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<NewFtpShiborInterestRateDraft> list = page.getRecords();
        List<NewFtpDetailTreasuryBondYieldListRSP> rsp = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(list)) {
            switch (ftpFrequency) {
                case DAY:
                    list.forEach(day -> {
                        NewFtpDetailTreasuryBondYieldListRSP body = new NewFtpDetailTreasuryBondYieldListRSP();
                        body.setDate(day.getDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
                        body.setValue(day.getValue());
                        body.setId(day.getId());
                        rsp.add(body);
                    });
                    break;
                case MONTH:
                    list.forEach(day -> {
                        NewFtpDetailTreasuryBondYieldListRSP body = new NewFtpDetailTreasuryBondYieldListRSP();
                        body.setDate(day.getDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_MONTH_PATTERN)));
                        body.setValue(day.getValue());
                        body.setId(day.getId());
                        rsp.add(body);
                    });
                    break;
                case SEASON:
                    list.forEach(day -> {
                        NewFtpDetailTreasuryBondYieldListRSP body = new NewFtpDetailTreasuryBondYieldListRSP();
                        LocalDate date = day.getDate();
                        body.setDate(String.format("%s-Q%s", date.getYear(), DateUtil.ensureQuarter(date.getMonthValue())));
                        body.setValue(day.getValue());
                        body.setId(day.getId());
                        rsp.add(body);
                    });
                    break;
                default:
            }
        }
        return PageR.of(rsp, page.getTotal());
    }

    /**
     * 从配置区拉取数据
     */
    @Transactional(rollbackFor = Throwable.class)
    public void addShiborInterestRate(Long ftpId){
        NewFtpBaseInfo newFtpBaseInfo = newFtpBaseInfoMapper.selectById(ftpId);
        if(ObjectUtil.isEmpty(newFtpBaseInfo)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //清理已有数据
        SpringContextUtil.getBean(NewFtpShiborInterestRateDraftService.class).remove(Wrappers.<NewFtpShiborInterestRateDraft>lambdaQuery()
                .eq(NewFtpShiborInterestRateDraft::getFtpId, ftpId));
        List<NewFtpShiborInterestRateDraft> dayDraft = new ArrayList<>();
        List<NewFtpShiborInterestRateDraft> monthDraft = new ArrayList<>();
        List<NewFtpShiborInterestRateDraft> seasonDraft = new ArrayList<>();

        List<NewFtpShiborInterestRateConfig> newFtpShiborInterestRateConfigs = newFtpShiborInterestRateConfigMapper.selectList(Wrappers.<NewFtpShiborInterestRateConfig>lambdaQuery()
                .between(NewFtpShiborInterestRateConfig::getDate, newFtpBaseInfo.getMonth().minusMonths(9).atStartOfDay(), newFtpBaseInfo.getMonth().with(TemporalAdjusters.lastDayOfMonth())));
        if(ObjectUtil.isNotEmpty(newFtpShiborInterestRateConfigs)){
            //添加天
            newFtpShiborInterestRateConfigs.forEach(config -> {
                NewFtpShiborInterestRateDraft draft = BeanUtil.copyProperties(config, NewFtpShiborInterestRateDraft.class, "id");
                draft.setFtpId(ftpId);
                draft.setFrequency(FtpFrequency.DAY.name());
                dayDraft.add(draft);
            });
            //添加月
            ave(dayDraft.stream().collect(Collectors.groupingBy(e -> e.getDate().with(TemporalAdjusters.firstDayOfMonth())))).forEach((k, v) -> {
                NewFtpShiborInterestRateDraft draft = new NewFtpShiborInterestRateDraft();
                draft.setDate(k);
                draft.setValue(v);
                draft.setFtpId(ftpId);
                draft.setFrequency(FtpFrequency.MONTH.name());
                monthDraft.add(draft);
            });
            //季度
            ave(monthDraft.stream().collect(Collectors.groupingBy(e -> e.getDate().minusMonths(e.getDate().getMonthValue() % 3 == 0 ? 2 : e.getDate().getMonthValue() % 3 - 1)))).forEach((k, v) -> {
                NewFtpShiborInterestRateDraft draft = new NewFtpShiborInterestRateDraft();
                draft.setDate(k);
                draft.setValue(v);
                draft.setFtpId(ftpId);
                draft.setFrequency(FtpFrequency.SEASON.name());
                seasonDraft.add(draft);
            });
            List<NewFtpShiborInterestRateDraft> addList = new ArrayList<>();
            addList.addAll(dayDraft);
            addList.addAll(monthDraft);
            addList.addAll(seasonDraft);
            if(!addList.isEmpty()){
                SpringContextUtil.getBean(NewFtpShiborInterestRateDraftService.class).saveBatch(addList);
            }
        }
        //添加波动值
        newFtpShiborInterestRatePricingDraftService.addTreasuryBondYield(ftpId);
    }

    private Map<LocalDate, Integer> ave(Map<LocalDate, List<NewFtpShiborInterestRateDraft>> dateListMap){
        if(ObjectUtil.isEmpty(dateListMap)){
            return Collections.emptyMap();
        }
        Map<LocalDate, Integer> map = new LinkedHashMap<>();
        dateListMap.forEach((k, v) -> {
            if (!v.isEmpty()) {
                Integer reduce = v.stream().map(NewFtpShiborInterestRateDraft::getValue).reduce(0, Integer::sum);
                map.put(k, new BigDecimal(reduce).divide(new BigDecimal(v.size()), 0, RoundingMode.HALF_UP).intValue());
            }
        });
        return map;
    }

}

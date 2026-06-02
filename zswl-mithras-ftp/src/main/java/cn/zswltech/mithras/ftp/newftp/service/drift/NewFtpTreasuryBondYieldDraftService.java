package cn.zswltech.mithras.ftp.newftp.service.drift;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.spring.SpringContextUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.newftp.NewFtpCommonDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailTreasuryBondYieldListRSP;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpFrequency;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.ftp.newftp.mapper.NewFtpBaseInfoMapper;
import cn.zswltech.mithras.ftp.newftp.mapper.config.NewFtpTreasuryBondYieldConfigMapper;
import cn.zswltech.mithras.ftp.newftp.mapper.draft.NewFtpTreasuryBondYieldDraftMapper;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpTreasuryBondYieldConfig;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpTreasuryBondYieldDraft;
import cn.zswltech.mithras.ftp.newftp.service.drift.NewFtpTreasuryBondYieldPricingDraftService;
import cn.zswltech.mithras.ftp.newftp.service.lib.NewFtpTreasuryBondYieldLibService;
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
 * @description 10年期国债收益率
 * @date 2023-05-21
 */
@Service
public class NewFtpTreasuryBondYieldDraftService
        extends ServiceImpl<NewFtpTreasuryBondYieldDraftMapper, NewFtpTreasuryBondYieldDraft> {

    @Resource
    private NewFtpTreasuryBondYieldConfigMapper newFtpTreasuryBondYieldConfigMapper;
    @Resource
    private NewFtpTreasuryBondYieldLibService newFtpTreasuryBondYieldLibService;
    @Resource
    private NewFtpBaseInfoMapper newFtpBaseInfoMapper;
    @Resource
    private NewFtpTreasuryBondYieldPricingDraftService newFtpTreasuryBondYieldPricingDraftService;

    /**
     * 查询十年期国债
     **/
    public PageR<NewFtpDetailTreasuryBondYieldListRSP> treasuryBondYieldList(NewFtpCommonDetailReq req) {
        FtpFrequency ftpFrequency = ofNullable(FtpFrequency.of(req.getFrequency())).orElseThrow(() -> new MithrasException("暂不支持"));
        Page<NewFtpTreasuryBondYieldDraft> page;
        if (ObjectUtil.isEmpty(req.getVersion())) {
            page = this.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<NewFtpTreasuryBondYieldDraft>lambdaQuery()
                    .eq(NewFtpTreasuryBondYieldDraft::getFtpId, req.getMainId())
                    .eq(NewFtpTreasuryBondYieldDraft::getFrequency, req.getFrequency())
                    .orderByDesc(NewFtpTreasuryBondYieldDraft::getDate));
        } else {
            page = newFtpTreasuryBondYieldLibService.getByVersion(req.getMainId(), req.getFrequency(), req.getVersion(), req.getPage(), req.getPageSize());
        }
        if (ObjectUtil.isEmpty(page) && ObjectUtil.isNotEmpty(page.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<NewFtpTreasuryBondYieldDraft> list = page.getRecords();
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
                        body.setDate(String.format("%s-Q%s", date.getYear(), (date.getMonthValue() + 2) / 3));
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

    //从配置区拉取数据
    @Transactional(rollbackFor = Throwable.class)
    public void addTreasuryBondYield(Long ftpId){
        NewFtpBaseInfo newFtpBaseInfo = newFtpBaseInfoMapper.selectById(ftpId);
        if(ObjectUtil.isEmpty(newFtpBaseInfo)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //清理已有数据
        SpringContextUtil.getBean(NewFtpTreasuryBondYieldDraftService.class).remove(Wrappers.<NewFtpTreasuryBondYieldDraft>lambdaQuery()
        .eq(NewFtpTreasuryBondYieldDraft::getFtpId, ftpId));
        List<NewFtpTreasuryBondYieldDraft> dayDraft = new ArrayList<>();
        List<NewFtpTreasuryBondYieldDraft> monthDraft = new ArrayList<>();
        List<NewFtpTreasuryBondYieldDraft> seasonDraft = new ArrayList<>();

        List<NewFtpTreasuryBondYieldConfig> newFtpTreasuryBondYieldConfigs = newFtpTreasuryBondYieldConfigMapper.selectList(Wrappers.<NewFtpTreasuryBondYieldConfig>lambdaQuery()
                .between(NewFtpTreasuryBondYieldConfig::getDate, newFtpBaseInfo.getMonth().minusMonths(9).atStartOfDay(), newFtpBaseInfo.getMonth().with(TemporalAdjusters.lastDayOfMonth())));
        if(ObjectUtil.isNotEmpty(newFtpTreasuryBondYieldConfigs)){
            //添加天
            newFtpTreasuryBondYieldConfigs.forEach(config -> {
                NewFtpTreasuryBondYieldDraft draft = BeanUtil.copyProperties(config, NewFtpTreasuryBondYieldDraft.class, "id");
                draft.setFtpId(ftpId);
                draft.setFrequency(FtpFrequency.DAY.name());
                dayDraft.add(draft);
            });
            //添加月
            aveMonth(dayDraft.stream().collect(Collectors.groupingBy(e -> e.getDate().with(TemporalAdjusters.firstDayOfMonth())))).forEach((k, v) -> {
                NewFtpTreasuryBondYieldDraft draft = new NewFtpTreasuryBondYieldDraft();
                draft.setDate(k);
                draft.setValue(v);
                draft.setFtpId(ftpId);
                draft.setFrequency(FtpFrequency.MONTH.name());
                monthDraft.add(draft);
            });
            //季度
            aveMonth(monthDraft.stream().collect(Collectors.groupingBy(e -> e.getDate().minusMonths(e.getDate().getMonthValue() % 3 == 0 ? 2 : e.getDate().getMonthValue() % 3 - 1)))).forEach((k, v) -> {
                NewFtpTreasuryBondYieldDraft draft = new NewFtpTreasuryBondYieldDraft();
                draft.setDate(k);
                draft.setValue(v);
                draft.setFtpId(ftpId);
                draft.setFrequency(FtpFrequency.SEASON.name());
                seasonDraft.add(draft);
            });
            List<NewFtpTreasuryBondYieldDraft> addList = new ArrayList<>();
            addList.addAll(dayDraft);
            addList.addAll(monthDraft);
            addList.addAll(seasonDraft);
            if(!addList.isEmpty()){
                SpringContextUtil.getBean(NewFtpTreasuryBondYieldDraftService.class).saveBatch(addList);
            }
        }
        //添加波动值
        newFtpTreasuryBondYieldPricingDraftService.addTreasuryBondYieldPricing(ftpId);
    }

    private Map<LocalDate, Integer> aveMonth(Map<LocalDate, List<NewFtpTreasuryBondYieldDraft>> dateListMap){
        if(ObjectUtil.isEmpty(dateListMap)){
            return Collections.emptyMap();
        }
        Map<LocalDate, Integer> map = new LinkedHashMap<>();
        dateListMap.forEach((k, v) -> {
            if (!v.isEmpty()) {
                Integer reduce = v.stream().map(NewFtpTreasuryBondYieldDraft::getValue).reduce(0, Integer::sum);
                map.put(k, new BigDecimal(reduce).divide(new BigDecimal(v.size()), 0, RoundingMode.HALF_UP).intValue());
            }
        });
        return map;
    }

}

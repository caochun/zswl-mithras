package cn.zswltech.mithras.ftp.newftp.service.drift;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.spring.SpringContextUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.newftp.NewFtpCommonDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailLprPricingListRSP;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpFrequency;
import cn.zswltech.mithras.service.mapper.basedata.BaseDataLprMapper;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataLpr;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.ftp.newftp.mapper.NewFtpBaseInfoMapper;
import cn.zswltech.mithras.ftp.newftp.mapper.draft.NewFtpLprPricingDraftMapper;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpLprPricingDraft;
import cn.zswltech.mithras.ftp.newftp.service.lib.NewFtpLprPricingLibService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * @description LPR定价
 * @date 2023-05-21
 */
@Service
public class NewFtpLprPricingDraftService extends ServiceImpl<NewFtpLprPricingDraftMapper, NewFtpLprPricingDraft> {

    @Resource
    private BaseDataLprMapper baseDataLprMapper;
    @Resource
    private NewFtpBaseInfoMapper newFtpBaseInfoMapper;
    @Resource
    private NewFtpLprPricingLibService newFtpLprPricingLibService;

    public PageR<NewFtpDetailLprPricingListRSP> lprPricingList(NewFtpCommonDetailReq req) {
        FtpFrequency ftpFrequency = ofNullable(FtpFrequency.of(req.getFrequency())).orElseThrow(() -> new MithrasException("暂不支持"));
        List<NewFtpDetailLprPricingListRSP> rsp = new ArrayList<>();
        Page<NewFtpLprPricingDraft> page;
        if (ObjectUtil.isEmpty(req.getVersion())) {
            page = this.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<NewFtpLprPricingDraft>lambdaQuery()
                    .eq(NewFtpLprPricingDraft::getFtpId, req.getMainId())
                    .eq(NewFtpLprPricingDraft::getFrequency, req.getFrequency())
                    .orderByDesc(NewFtpLprPricingDraft::getLprDate));
        } else {
            page = newFtpLprPricingLibService.getByVersion(req.getMainId(), req.getFrequency(), req.getVersion(), req.getPage(), req.getPageSize());
        }
        if (ObjectUtil.isEmpty(page) && ObjectUtil.isNotEmpty(page.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<NewFtpLprPricingDraft> list = page.getRecords();
        if (ObjectUtil.isNotEmpty(list)) {
            switch (ftpFrequency) {
                case MONTH:
                    list.forEach(day -> {
                        NewFtpDetailLprPricingListRSP body = new NewFtpDetailLprPricingListRSP();
                        body.setLprDate(day.getLprDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_MONTH_PATTERN)));
                        body.setOneYear(day.getOneYear());
                        body.setFiveYear(day.getFiveYear());
                        body.setId(day.getId());
                        rsp.add(body);
                    });
                    break;
                case SEASON:
                    list.forEach(day -> {
                        NewFtpDetailLprPricingListRSP body = new NewFtpDetailLprPricingListRSP();
                        LocalDate date = day.getLprDate();
                        body.setLprDate(String.format("%s-Q%s", date.getYear(), (date.getMonthValue() + 2) / 3));
                        body.setOneYear(day.getOneYear());
                        body.setFiveYear(day.getFiveYear());
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
    public void addTreasuryBondYield(Long ftpId) {
        NewFtpBaseInfo newFtpBaseInfo = newFtpBaseInfoMapper.selectById(ftpId);
        if (ObjectUtil.isEmpty(newFtpBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //清理已有数据
        SpringContextUtil.getBean(NewFtpLprPricingDraftService.class).remove(Wrappers.<NewFtpLprPricingDraft>lambdaQuery()
                .eq(NewFtpLprPricingDraft::getFtpId, ftpId));
        List<NewFtpLprPricingDraft> monthDraft = new ArrayList<>();
        List<NewFtpLprPricingDraft> seasonDraft;

        List<BaseDataLpr> baseDataLprs = baseDataLprMapper.selectList(Wrappers.<BaseDataLpr>lambdaQuery()
                .between(BaseDataLpr::getLprDate, newFtpBaseInfo.getMonth().minusMonths(9).atStartOfDay(), newFtpBaseInfo.getMonth().with(TemporalAdjusters.lastDayOfMonth())));
        if (ObjectUtil.isNotEmpty(baseDataLprs)) {
            //添加月
            baseDataLprs.forEach(config -> {
                NewFtpLprPricingDraft draft = BeanUtil.copyProperties(config, NewFtpLprPricingDraft.class, "id");
                draft.setFtpId(ftpId);
                draft.setFrequency(FtpFrequency.MONTH.name());
                monthDraft.add(draft);
            });

            //季度
            seasonDraft = ave(monthDraft.stream().collect(Collectors.groupingBy(e -> e.getLprDate().minusMonths(e.getLprDate().getMonthValue() % 3 == 0 ? 2 : e.getLprDate().getMonthValue() % 3 - 1).with(TemporalAdjusters.firstDayOfMonth()))));
            List<NewFtpLprPricingDraft> addList = new java.util.ArrayList<>();
            addList.addAll(monthDraft);
            addList.addAll(seasonDraft);
            if (!addList.isEmpty()) {
                SpringContextUtil.getBean(NewFtpLprPricingDraftService.class).saveBatch(addList);
            }
        }
    }

    private List<NewFtpLprPricingDraft> ave(Map<LocalDate, List<NewFtpLprPricingDraft>> dateListMap) {
        List<NewFtpLprPricingDraft> draftList = new ArrayList<>();
        if (ObjectUtil.isEmpty(dateListMap)) {
            return draftList;
        }
        dateListMap.forEach((k, v) -> {
            if (!v.isEmpty()) {
                BigDecimal oneYear = v.stream().map(e -> new BigDecimal(e.getOneYear())).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal fiveYear = v.stream().map(e -> new BigDecimal(e.getFiveYear())).reduce(BigDecimal.ZERO, BigDecimal::add);
                NewFtpLprPricingDraft draft = new NewFtpLprPricingDraft();
                draft.setFtpId(v.get(0).getFtpId());
                draft.setLprDate(k);
                draft.setFrequency(FtpFrequency.SEASON.name());
                draft.setOneYear(oneYear.divide(new BigDecimal(v.size()), 2, RoundingMode.HALF_UP).toPlainString());
                draft.setFiveYear(fiveYear.divide(new BigDecimal(v.size()), 2, RoundingMode.HALF_UP).toPlainString());
                draftList.add(draft);
            }
        });
        return draftList;
    }

}

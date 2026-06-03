package cn.zswltech.mithras.finance.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.finance.FinanceProfitConfirmREQ;
import cn.zswltech.mithras.dto.finance.FinanceProjectProfitRSP;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.finance.mapper.finance.FinanceProjectProfitMapper;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceProjectProfit;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/6/16
 * @description
 */
@Slf4j
@Service
public class FinanceProjectProfitService extends ServiceImpl<FinanceProjectProfitMapper, FinanceProjectProfit> {

    @Resource
    private FinanceProjectProfitService thisService;
//    @Resource
//    private FtpPriceInfoService ftpPriceInfoService;

    public PageR<FinanceProjectProfitRSP> pageList(PageReq req) {
        Page<FinanceProjectProfit> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        Page<FinanceProjectProfit> dbResult = this.page(pageQuery, null);
        if (CollectionUtil.isEmpty(dbResult.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<FinanceProjectProfitRSP> rspList = dbResult.getRecords().stream()
                .map(item -> BeanUtil.copyProperties(item, FinanceProjectProfitRSP.class))
                .collect(Collectors.toList());
        return PageR.of(rspList, dbResult.getTotal(), req.getPage(), req.getPageSize());
    }

    public FinanceProjectProfit getOneByYearMonth(int year, int month) {
        LambdaQueryWrapper<FinanceProjectProfit> query = Wrappers.lambdaQuery();
        query.eq(FinanceProjectProfit::getYear, year);
        query.eq(FinanceProjectProfit::getMonth, month);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void confirm(FinanceProfitConfirmREQ req) {
        List<FinanceProjectProfit> financeProjectProfits = thisService.listByIds(req.getProjectProfitIdList());
        if (financeProjectProfits.isEmpty()) {
            throw new MithrasException("利润记录不存在");
        }

        if (financeProjectProfits.size() < req.getProjectProfitIdList().size()) {
            throw new MithrasException("实际数据与预期不符，不允许操作");
        }

        thisService.lambdaUpdate()
                .in(FinanceProjectProfit::getId, financeProjectProfits.stream().map(FinanceProjectProfit::getId).collect(Collectors.toList()))
                .set(FinanceProjectProfit::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                .update();

//        // 这里还要去更新FTP价格表中相应的数据，数据量不大，所以先循环更新
//        for (FinanceProjectProfit financeProjectProfit : financeProjectProfits) {
//            LocalDate recordDateStart = LocalDate.of(financeProjectProfit.getYear(), financeProjectProfit.getMonth(), 1);
//            ftpPriceInfoService.lambdaUpdate()
//                    .set(FtpPriceInfo::getCostIsConfirmed, YesOrNoNumberEnum.YES.getCode())
//                    .ge(FtpPriceInfo::getRecordDate, recordDateStart)
//                    .le(FtpPriceInfo::getRecordDate, recordDateStart.with(TemporalAdjusters.lastDayOfMonth()))
//                    .update();
//        }
//        log.info("确认利润成功, 本次修改数据年月【{}】", financeProjectProfits.stream().map(item -> String.format("%d-%02d", item.getYear(), item.getMonth())).collect(Collectors.toList()));
    }
}

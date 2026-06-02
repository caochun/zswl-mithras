package cn.zswltech.mithras.ftp.newftp.service.lib;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.ftp.newftp.lib.impl.NewFtpFinancingCostPricingLibHandler;
import cn.zswltech.mithras.ftp.newftp.mapper.lib.NewFtpFinancingCostPricingLibMapper;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpFinancingCostPricingDraft;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpFinancingCostPricingLib;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author yangxiong
* @description 针对表【new_ftp_financing_cost_pricing_lib(融资成本版本表)】的数据库操作Service实现
* @createDate 2024-03-22 14:00:38
*/
@Service
public class NewFtpFinancingCostPricingLibService extends ServiceImpl<NewFtpFinancingCostPricingLibMapper, NewFtpFinancingCostPricingLib> {

    @Resource
    private NewFtpFinancingCostPricingLibHandler newFtpFinancingCostPricingLibHandler;

    public Page<NewFtpFinancingCostPricingDraft> getByVersion(Long ftpId, String version, Integer page, Integer pageSize) {
        Page<NewFtpFinancingCostPricingLib> newFtpLprPricingPage = this.page(new Page<>(page, pageSize), Wrappers.<NewFtpFinancingCostPricingLib>lambdaQuery()
                .select(NewFtpFinancingCostPricingLib::getMonth)
                .eq(NewFtpFinancingCostPricingLib::getFtpId, ftpId)
                .groupBy(NewFtpFinancingCostPricingLib::getMonth)
                .orderByDesc(NewFtpFinancingCostPricingLib::getMonth));
        //查询所有
        List<LocalDate> months = newFtpLprPricingPage.getRecords().stream().map(NewFtpFinancingCostPricingDraft::getMonth).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(months)) {
            return null;
        }
        LambdaQueryWrapper<NewFtpFinancingCostPricingLib> query = Wrappers.lambdaQuery();
        query.eq(NewFtpFinancingCostPricingLib::getFtpId, ftpId);
        query.eq(NewFtpFinancingCostPricingLib::getVersion, version);
        query.in(NewFtpFinancingCostPricingLib::getMonth, months);
        query.orderByDesc(NewFtpFinancingCostPricingLib::getId);
        List<NewFtpFinancingCostPricingLib> pricingLibPage = this.list(query);
        Page<NewFtpFinancingCostPricingDraft> rspPage = new Page<>();
        if(ObjectUtil.isNotEmpty(pricingLibPage)){
            rspPage.setRecords(pricingLibPage.stream().map(newFtpFinancingCostPricingLibHandler::actualLib2Entity).collect(Collectors.toList()));
        }
        rspPage.setTotal(newFtpLprPricingPage.getTotal());
        rspPage.setSize(newFtpLprPricingPage.getSize());
        rspPage.setCurrent(newFtpLprPricingPage.getCurrent());
        return rspPage;
    }
}





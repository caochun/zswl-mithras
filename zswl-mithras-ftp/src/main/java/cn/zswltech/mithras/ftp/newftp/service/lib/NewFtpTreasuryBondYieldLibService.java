package cn.zswltech.mithras.ftp.newftp.service.lib;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.ftp.newftp.lib.impl.NewFtpTreasuryBondYieldLibHandler;
import cn.zswltech.mithras.ftp.newftp.mapper.lib.NewFtpTreasuryBondYieldLibMapper;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpTreasuryBondYieldDraft;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpTreasuryBondYieldLib;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.stream.Collectors;

/**
* @author yangxiong
* @description 针对表【new_ftp_treasury_bond_yield_lib(十年期国债收益率版本表)】的数据库操作Service实现
* @createDate 2024-03-22 14:00:38
*/
@Service
public class NewFtpTreasuryBondYieldLibService extends ServiceImpl<NewFtpTreasuryBondYieldLibMapper, NewFtpTreasuryBondYieldLib> {

    @Resource
    private NewFtpTreasuryBondYieldLibHandler newFtpTreasuryBondYieldLibHandler;

    public Page<NewFtpTreasuryBondYieldDraft> getByVersion(Long ftpId, String frequency, String version, Integer page, Integer pageSize) {
        LambdaQueryWrapper<NewFtpTreasuryBondYieldLib> query = Wrappers.lambdaQuery();
        query.eq(NewFtpTreasuryBondYieldLib::getFtpId, ftpId);
        query.eq(NewFtpTreasuryBondYieldLib::getFrequency, frequency);
        query.eq(NewFtpTreasuryBondYieldLib::getVersion, version);
        query.orderByDesc(NewFtpTreasuryBondYieldLib::getId);
        Page<NewFtpTreasuryBondYieldLib> pricingLibPage = this.page(new Page<>(page, pageSize), query);
        Page<NewFtpTreasuryBondYieldDraft> rspPage = new Page<>();
        if (ObjectUtil.isNotEmpty(pricingLibPage) && ObjectUtil.isNotEmpty(pricingLibPage.getRecords())) {
            rspPage.setRecords(pricingLibPage.getRecords().stream().map(newFtpTreasuryBondYieldLibHandler::actualLib2Entity).collect(Collectors.toList()));
            rspPage.setTotal(pricingLibPage.getTotal());
            rspPage.setSize(pricingLibPage.getSize());
            rspPage.setCurrent(pricingLibPage.getCurrent());
        }
        return rspPage;
    }

}





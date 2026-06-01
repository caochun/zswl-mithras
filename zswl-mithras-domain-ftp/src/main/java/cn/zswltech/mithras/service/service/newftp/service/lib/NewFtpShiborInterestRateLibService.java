package cn.zswltech.mithras.service.service.newftp.service.lib;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.service.newftp.lib.impl.NewFtpShiborInterestRateLibHandler;
import cn.zswltech.mithras.service.service.newftp.mapper.lib.NewFtpShiborInterestRateLibMapper;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpShiborInterestRateDraft;
import cn.zswltech.mithras.service.service.newftp.model.lib.NewFtpShiborInterestRateLib;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.stream.Collectors;

/**
* @author yangxiong
* @description 针对表【new_ftp_shibor_interest_rate_lib(一年期shibor利率版本表)】的数据库操作Service实现
* @createDate 2024-03-22 14:00:38
*/
@Service
public class NewFtpShiborInterestRateLibService extends ServiceImpl<NewFtpShiborInterestRateLibMapper, NewFtpShiborInterestRateLib> {

    @Resource
    private NewFtpShiborInterestRateLibHandler newFtpShiborInterestRateLibHandler;

    public Page<NewFtpShiborInterestRateDraft> getByVersion(Long ftpId, String frequency,String version, Integer page, Integer pageSize) {
        LambdaQueryWrapper<NewFtpShiborInterestRateLib> query = Wrappers.lambdaQuery();
        query.eq(NewFtpShiborInterestRateLib::getFtpId, ftpId);
        query.eq(NewFtpShiborInterestRateLib::getVersion, version);
        query.eq(NewFtpShiborInterestRateLib::getFrequency, frequency);
        query.orderByDesc(NewFtpShiborInterestRateLib::getId);
        Page<NewFtpShiborInterestRateLib> pricingLibPage = this.page(new Page<>(page, pageSize), query);
        Page<NewFtpShiborInterestRateDraft> rspPage = new Page<>();
        if(ObjectUtil.isNotEmpty(pricingLibPage) && ObjectUtil.isNotEmpty(pricingLibPage.getRecords())){
            rspPage.setRecords(pricingLibPage.getRecords().stream().map(newFtpShiborInterestRateLibHandler::actualLib2Entity).collect(Collectors.toList()));
            rspPage.setTotal(pricingLibPage.getTotal());
            rspPage.setSize(pricingLibPage.getSize());
            rspPage.setCurrent(pricingLibPage.getCurrent());
        }
        return rspPage;
    }

}





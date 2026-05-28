package cn.zswltech.mithras.service.service.newftp.service.lib;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.service.newftp.lib.impl.NewFtpGuaranteeCostPricingLibHandler;
import cn.zswltech.mithras.service.service.newftp.mapper.lib.NewFtpGuaranteeCostPricingLibMapper;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpGuaranteeCostPricingDraft;
import cn.zswltech.mithras.service.service.newftp.model.lib.NewFtpGuaranteeCostPricingLib;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.stream.Collectors;

/**
* @author yangxiong
* @description 针对表【new_ftp_guarantee_cost_pricing_lib(担保成本版本表)】的数据库操作Service实现
* @createDate 2024-03-22 14:00:38
*/
@Service
public class NewFtpGuaranteeCostPricingLibService extends ServiceImpl<NewFtpGuaranteeCostPricingLibMapper, NewFtpGuaranteeCostPricingLib> {

    @Resource
    private NewFtpGuaranteeCostPricingLibHandler newFtpGuaranteeCostPricingLibHandler;
    public Page<NewFtpGuaranteeCostPricingDraft> getByVersion(Long ftpId, String version, Integer page, Integer pageSize) {
        LambdaQueryWrapper<NewFtpGuaranteeCostPricingLib> query = Wrappers.lambdaQuery();
        query.eq(NewFtpGuaranteeCostPricingLib::getFtpId, ftpId);
        query.eq(NewFtpGuaranteeCostPricingLib::getVersion, version);
        query.orderByDesc(NewFtpGuaranteeCostPricingLib::getMonth);
        Page<NewFtpGuaranteeCostPricingLib> pricingLibPage = this.page(new Page<>(page, pageSize), query);
        Page<NewFtpGuaranteeCostPricingDraft> rspPage = new Page<>();
        if(ObjectUtil.isNotEmpty(pricingLibPage) && ObjectUtil.isNotEmpty(pricingLibPage.getRecords())){
            rspPage.setRecords(pricingLibPage.getRecords().stream().map(newFtpGuaranteeCostPricingLibHandler::actualLib2Entity).collect(Collectors.toList()));
            rspPage.setTotal(pricingLibPage.getTotal());
            rspPage.setSize(pricingLibPage.getSize());
            rspPage.setCurrent(pricingLibPage.getCurrent());
        }
        return rspPage;
    }
}





package cn.zswltech.mithras.service.service.newftp.service.lib;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.service.newftp.lib.impl.NewFtpLprPricingLibHandler;
import cn.zswltech.mithras.service.service.newftp.mapper.lib.NewFtpLprPricingLibMapper;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpLprPricingDraft;
import cn.zswltech.mithras.service.service.newftp.model.lib.NewFtpLprPricingLib;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.stream.Collectors;

/**
* @author yangxiong
* @description 针对表【new_ftp_lpr_pricing_lib(lpr配置版本表)】的数据库操作Service实现
* @createDate 2024-03-22 14:00:38
*/
@Service
public class NewFtpLprPricingLibService extends ServiceImpl<NewFtpLprPricingLibMapper, NewFtpLprPricingLib> {
    @Resource
    private NewFtpLprPricingLibHandler newFtpLprPricingLibLibHandler;

    public Page<NewFtpLprPricingDraft> getByVersion(Long ftpId, String frequency, String version, Integer page, Integer pageSize) {
        LambdaQueryWrapper<NewFtpLprPricingLib> query = Wrappers.lambdaQuery();
        query.eq(NewFtpLprPricingLib::getFtpId, ftpId);
        query.eq(NewFtpLprPricingLib::getVersion, version);
        query.eq(NewFtpLprPricingLib::getFrequency, frequency);
        query.orderByDesc(NewFtpLprPricingLib::getId);
        Page<NewFtpLprPricingLib> pricingLibPage = this.page(new Page<>(page, pageSize), query);
        Page<NewFtpLprPricingDraft> rspPage = new Page<>();
        if(ObjectUtil.isNotEmpty(pricingLibPage) && ObjectUtil.isNotEmpty(pricingLibPage.getRecords())){
            rspPage.setRecords(pricingLibPage.getRecords().stream().map(newFtpLprPricingLibLibHandler::actualLib2Entity).collect(Collectors.toList()));
            rspPage.setTotal(pricingLibPage.getTotal());
            rspPage.setSize(pricingLibPage.getSize());
            rspPage.setCurrent(pricingLibPage.getCurrent());
        }
        return rspPage;
    }
}





package cn.zswltech.mithras.service.controller.ftp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.ftp.FtpIncomeBaseInfoApi;
import cn.zswltech.mithras.dto.ftp.*;
import cn.zswltech.mithras.service.mapper.model.fund.FundOrganization;
import cn.zswltech.mithras.service.service.ftp.FtpIncomeBaseInfoService;
import cn.zswltech.mithras.service.facade.fund.FundFacade;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
* @description 资金管理-融资管理-ftp收益表
* @author vico
* @date 2025-07-15
*/
@RestController
public class FtpIncomeBaseInfoController implements FtpIncomeBaseInfoApi {

    @Resource
    private FtpIncomeBaseInfoService ftpIncomeBaseInfoService;
    @Resource
    private FundFacade fundFacade;


    @Override
    public R<PageR<FtpIncomeBaseInfoListRSP>> list(FtpIncomeBaseInfoListREQ req){
        return R.ok(ftpIncomeBaseInfoService.list(req));
    }

    @Override
    public R<FtpIncomeBaseInfoListRSP> count(FtpIncomeBaseInfoListREQ req) {
        return R.ok(ftpIncomeBaseInfoService.ftpIncomeCount(req));
    }

    public R<FtpIncomeBaseInfoListRSP> detail(@RequestBody FtpIncomeDetailRecordListREQ req){
        return R.ok(ftpIncomeBaseInfoService.detail(req));
    }

    @Override
    public R<List<FtpIncomeDetailRecordListRSP>> recordList(FtpIncomeDetailRecordListREQ req){
        return R.ok(ftpIncomeBaseInfoService.recordList(req));
    }

    @Override
    public R<List<FtpIncomeOrganizationListRSP>> organizationList(@Valid FtpIncomeOrganizationListREQ req) {
        List<FundOrganization> fundOrganizations = fundFacade.getOrganizationsByName(req.getOrganizationName());
        List<FtpIncomeOrganizationListRSP> ftpIncomeOrganizationListRSPS = null;
        if(ObjectUtil.isNotEmpty(fundOrganizations)) {
            ftpIncomeOrganizationListRSPS = BeanUtil.copyToList(fundOrganizations, FtpIncomeOrganizationListRSP.class);
        }
        return R.ok(ftpIncomeOrganizationListRSPS);
    }


}
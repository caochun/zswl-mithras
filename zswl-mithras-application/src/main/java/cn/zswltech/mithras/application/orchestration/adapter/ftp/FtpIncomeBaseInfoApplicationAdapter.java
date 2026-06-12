package cn.zswltech.mithras.application.orchestration.adapter.ftp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.ftp.FtpIncomeBaseInfoListREQ;
import cn.zswltech.mithras.dto.ftp.FtpIncomeBaseInfoListRSP;
import cn.zswltech.mithras.dto.ftp.FtpIncomeDetailRecordListREQ;
import cn.zswltech.mithras.dto.ftp.FtpIncomeDetailRecordListRSP;
import cn.zswltech.mithras.dto.ftp.FtpIncomeOrganizationListREQ;
import cn.zswltech.mithras.dto.ftp.FtpIncomeOrganizationListRSP;
import cn.zswltech.mithras.ftp.oldftp.service.application.FtpIncomeBaseInfoApplicationService;
import cn.zswltech.mithras.fund.persistence.model.FundOrganization;
import cn.zswltech.mithras.application.orchestration.ftp.FtpIncomeBaseInfoService;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FtpIncomeBaseInfoApplicationAdapter implements FtpIncomeBaseInfoApplicationService {

    @Resource
    private FtpIncomeBaseInfoService ftpIncomeBaseInfoService;
    @Resource
    private FundOrganizationService fundOrganizationService;

    @Override
    public PageR<FtpIncomeBaseInfoListRSP> list(FtpIncomeBaseInfoListREQ req) {
        return ftpIncomeBaseInfoService.list(req);
    }

    @Override
    public FtpIncomeBaseInfoListRSP ftpIncomeCount(FtpIncomeBaseInfoListREQ req) {
        return ftpIncomeBaseInfoService.ftpIncomeCount(req);
    }

    @Override
    public FtpIncomeBaseInfoListRSP detail(FtpIncomeDetailRecordListREQ req) {
        return ftpIncomeBaseInfoService.detail(req);
    }

    @Override
    public List<FtpIncomeDetailRecordListRSP> recordList(FtpIncomeDetailRecordListREQ req) {
        return ftpIncomeBaseInfoService.recordList(req);
    }

    @Override
    public List<FtpIncomeOrganizationListRSP> organizationList(FtpIncomeOrganizationListREQ req) {
        List<FundOrganization> fundOrganizations = fundOrganizationService.listByOrganizationName(req.getOrganizationName());
        return ObjectUtil.isNotEmpty(fundOrganizations) ? BeanUtil.copyToList(fundOrganizations, FtpIncomeOrganizationListRSP.class) : null;
    }
}

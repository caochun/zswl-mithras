package cn.zswltech.mithras.ftp.oldftp.service.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.ftp.FtpIncomeBaseInfoListREQ;
import cn.zswltech.mithras.dto.ftp.FtpIncomeBaseInfoListRSP;
import cn.zswltech.mithras.dto.ftp.FtpIncomeDetailRecordListREQ;
import cn.zswltech.mithras.dto.ftp.FtpIncomeDetailRecordListRSP;
import cn.zswltech.mithras.dto.ftp.FtpIncomeOrganizationListREQ;
import cn.zswltech.mithras.dto.ftp.FtpIncomeOrganizationListRSP;

import java.util.List;

public interface FtpIncomeBaseInfoApplicationService {

    PageR<FtpIncomeBaseInfoListRSP> list(FtpIncomeBaseInfoListREQ req);

    FtpIncomeBaseInfoListRSP ftpIncomeCount(FtpIncomeBaseInfoListREQ req);

    FtpIncomeBaseInfoListRSP detail(FtpIncomeDetailRecordListREQ req);

    List<FtpIncomeDetailRecordListRSP> recordList(FtpIncomeDetailRecordListREQ req);

    List<FtpIncomeOrganizationListRSP> organizationList(FtpIncomeOrganizationListREQ req);
}

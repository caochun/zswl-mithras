package cn.zswltech.mithras.finance.monthly.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.monthly.MonthlyManageApi;
import cn.zswltech.mithras.dto.monthly.*;

import javax.servlet.ServletOutputStream;

public interface MonthlyManageApplicationService extends MonthlyManageApi {

    void download(ServletOutputStream outputStream, MonthlyExcelREQ req);

    @Override
    default void download(MonthlyExcelREQ req) {
        throw new UnsupportedOperationException("HTTP download is handled by MonthlyManageController");
    }
}

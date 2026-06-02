package cn.zswltech.mithras.ftp.controller;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.ftp.FtpIncomeDetailRecordApi;
import cn.zswltech.mithras.dto.ftp.FtpIncomeDetailRecordRemoveREQ;
import cn.zswltech.mithras.ftp.service.FtpIncomeDetailRecordService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* @description 资金管理-融资管理-ftp收益记录表
* @author vico
* @date 2025-07-15
*/
@RestController
public class FtpIncomeDetailRecordController implements FtpIncomeDetailRecordApi {

    @Resource
    private FtpIncomeDetailRecordService ftpIncomeDetailRecordService;


    @Override
    public R<Void> remove(FtpIncomeDetailRecordRemoveREQ req){
        ftpIncomeDetailRecordService.remove(req);
        return R.ok();
    }

}
package cn.zswltech.mithras.service.service.workbench;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.workbench.WorkbenchShortcutsListRsp;
import cn.zswltech.mithras.ftp.oldftp.model.FtpMonthlyGuidance;
import cn.zswltech.mithras.service.mapper.model.workbench.WorkbenchShortcutBaseInfo;
import cn.zswltech.mithras.service.mapper.workbench.WorkbenchShortcutBaseInfoMapper;
import cn.zswltech.mithras.service.service.ftp.FtpMonthlyGuidanceService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
* @description 首页工作台-快捷方式-基本信息维护
* @author vico
* @date 2023-03-21
*/
@Service
public class WorkbenchShortcutBaseInfoService extends ServiceImpl<WorkbenchShortcutBaseInfoMapper, WorkbenchShortcutBaseInfo> {

    @Resource
    private FtpMonthlyGuidanceService ftpMonthlyGuidanceService;

    //需要特殊处理快捷功能
    public final static Set<String> shortcutSpecialCode;
    static {
        shortcutSpecialCode = new HashSet<>();
        //
        shortcutSpecialCode.add("88482");
    }

    public void shortcutSpecialHandle(String code, WorkbenchShortcutsListRsp rsp){
        switch (code) {
            case "88482" :
                //本月ftp
                LocalDate now = LocalDate.now();
                FtpMonthlyGuidance guidance = ftpMonthlyGuidanceService.getOne(Wrappers.<FtpMonthlyGuidance>lambdaQuery()
                        .eq(FtpMonthlyGuidance::getYear, now.getYear())
                        .eq(FtpMonthlyGuidance::getMonth, now.getMonth()));
                if(ObjectUtil.isNotEmpty(guidance)){
                    rsp.setPath(String.format("%s/detail/%s", rsp.getPath(), guidance.getId()));
                } else {
                    rsp.setMessage("本月FTP还未发布哦");
                }
                break;
            default:
        }
    }

}
package cn.zswltech.mithras.workbench.application;

import cn.zswltech.mithras.dto.workbench.WorkbenchShortcutsListRsp;
import cn.zswltech.mithras.workbench.mapper.WorkbenchShortcutBaseInfoMapper;
import cn.zswltech.mithras.workbench.mapper.model.WorkbenchShortcutBaseInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
* @description 首页工作台-快捷方式-基本信息维护
* @author vico
* @date 2023-03-21
*/
@Service
public class WorkbenchShortcutBaseInfoService extends ServiceImpl<WorkbenchShortcutBaseInfoMapper, WorkbenchShortcutBaseInfo> {

    @Resource
    private WorkbenchShortcutSpecialPort shortcutSpecialPort;

    //需要特殊处理快捷功能
    public final static Set<String> shortcutSpecialCode;
    static {
        shortcutSpecialCode = new HashSet<>();
        shortcutSpecialCode.add("88482");
    }

    public void shortcutSpecialHandle(String code, WorkbenchShortcutsListRsp rsp){
        switch (code) {
            case "88482" :
                Optional<Long> guidanceId = shortcutSpecialPort.getCurrentMonthlyFtpGuidanceId();
                if (guidanceId.isPresent()) {
                    rsp.setPath(String.format("%s/detail/%s", rsp.getPath(), guidanceId.get()));
                } else {
                    rsp.setMessage("本月FTP还未发布哦");
                }
                break;
            default:
        }
    }

}

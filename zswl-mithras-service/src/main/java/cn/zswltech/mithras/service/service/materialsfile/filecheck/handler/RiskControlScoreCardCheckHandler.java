package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;


import cn.hutool.core.lang.Pair;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;


/**
 * 默认校验
 * @author: jackerhe
 * @date: 2023/2/8 4:45 下午
 **/
@Component
public class RiskControlScoreCardCheckHandler extends FileModuleCheck {

    @Resource
    private SysUserService sysUserService;


    private static final String FXGLB = "FXGLB_YWPS";
    private static final String FLHGB = "FLHGB_ZCBQ";

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.RISK_CONTROL_SCORE_CARD.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        List<OrgDO> userDeptList = sysUserService.getUserDeptList();
        for(OrgDO orgDO : userDeptList){
            if(FXGLB.equals(orgDO.getCode()) || FLHGB.equals(orgDO.getCode())){
                return;
            }
        }
        throw new MithrasException("仅风控部门全员可上传!");
    }



    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        List<OrgDO> userDeptList = sysUserService.getUserDeptList();
        for(OrgDO orgDO : userDeptList){
            if(FXGLB.equals(orgDO.getCode()) || FLHGB.equals(orgDO.getCode())){
                return;
            }
        }
        throw new MithrasException("仅风控部门全员可删除!");
    }

    @Override
    public void checkList(String moduleKey, Long fileId) {
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds) {
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileIds) {
    }
}

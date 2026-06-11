package cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.handler;


import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;


/**
 * @ClassName AfterLeaseCollectionCheck
 * 租后催收上传文件检查
 * @Author jackerhe
 * @Date 2022/11/20 11:51 上午
 * @Version 1.0
 **/
@Component
public class AfterLeaseCollectionCheckHandler extends FileModuleCheck {

    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private SysUserService sysUserService;

    private static final String FXGLB = "FXGLB_YWPS";
    private static final String FLHGB = "FLHGB_ZCBQ";

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.OVERDUE_COLLECTION_REDUCTION.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        //该合同的主办和风控经理可上传相关附件
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(mainId);
        if(ObjectUtil.isNull(contractBaseInfo)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        List<OrgDO> userDeptList = sysUserService.getUserDeptList();
        for(OrgDO orgDO : userDeptList){
            if(FXGLB.equals(orgDO.getCode()) || FLHGB.equals(orgDO.getCode())){
                return;
            }
        }
        if(ObjectUtil.equals(contractBaseInfo.getProjSponsorUserId(), AccountUtil.getLoginInfo().getId())){
            return;
        }
        throw new MithrasException("仅项目主办和风控部门全员可上传!");
    }



    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        //只有上传人可删除
        MaterialsList materialsList = materialsListService.getById(fileId);
        if(!ObjectUtil.equals(materialsList.getCreateBy(), AccountUtil.getLoginInfo().getId())){
            throw new MithrasException("仅上传人可删除!");
        }
    }

    @Override
    public void checkList(String moduleKey, Long fileId) {
        //super.checkList(moduleKey, fileId);
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileId) {
        //下载权限同查看暂不限制
        super.checkDownload(moduleKey, mainId, fileId);
    }
}

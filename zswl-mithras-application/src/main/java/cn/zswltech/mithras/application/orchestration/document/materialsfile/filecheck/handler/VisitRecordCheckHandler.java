package cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.handler;


import cn.zswltech.mithras.application.orchestration.document.convert.FileConvert;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.customer.mapper.app.VisitRecordMapper;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.customer.mapper.model.app.VisitRecord;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.CLOSED;
import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.EXPIRE;


/**
 * @ClassName AfterLeaseCollectionCheck
 * 项目评审文件检查
 * @Author jackerhe
 * @Date 2022/11/20 11:51 上午
 * @Version 1.0
 **/
@Component
public class VisitRecordCheckHandler extends FileModuleCheck {


    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private VisitRecordMapper visitRecordMapper;
    @Resource
    protected FileConvert fileConvert;


    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.VISIT_RECORD.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        VisitRecord visitRecord = visitRecordMapper.selectById(mainId);
        authCheck(visitRecord);
        return;
    }


    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        // 先查询文档信息
        MaterialsList materialsList = materialsListService.getById(fileId);
        if (Objects.isNull(materialsList)) {
            throw new MithrasException("没有找到对应报告文件");
        }
        // 查询项目评审主数据
        VisitRecord visitRecord = visitRecordMapper.selectById(materialsList.getBelongId());
        authCheck(visitRecord);
    }

    @Override
    public void checkList(String moduleKey, Long fileId) {
        super.checkList(moduleKey, fileId);
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileId) {
        //下载权限同查看暂不限制
        super.checkDownload(moduleKey, mainId, fileId);
    }

    private void authCheck(VisitRecord visitRecord) {
        if (Objects.isNull(visitRecord)) {
            throw new MithrasException("现场尽调拜访记录不存在");
        }
    }



}

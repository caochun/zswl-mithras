package cn.zswltech.mithras.application.orchestration.document.file.impl;

import cn.hutool.core.lang.Pair;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.file.ext.FileListREQProjReviewExt;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.application.orchestration.document.file.AbstractFileListProvider;
import cn.zswltech.mithras.document.file.query.FileListExtQuery;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
/**
 * 项目评审
 *
 * @author wangchuanhao
 * @date 2023/2/6 1:41 PM
 */
@Component
public class ProjReviewFileListProvider extends AbstractFileListProvider {

    @Resource
    private FlowTaskApiService flowTaskApiService;

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.PROJ_REVIEW;
    }

    @Override
    public List<Pair<String, List<FileListRSP>>> listGroup(FileListREQ req) {
        FileListExtQuery extQuery = new FileListExtQuery();
        extQuery.setMaterialsTypes(ProjReviewMaterialsEnum.listAll());
//        if (Objects.nonNull(req.getExt())) {
//            FileListREQProjReviewExt extREQ = JSONObject.parseObject(JSON.toJSONString(req.getExt()), FileListREQProjReviewExt.class);
//            if (Objects.nonNull(extREQ.getProcessInstanceId())) {
//                ProcessResp processResp = flowTaskApiService.queryProcessById(extREQ.getProcessInstanceId());
//                ProcessModelTypeEnum byName = ProcessModelTypeEnum.getByName(processResp.getModelKey());
//                if (ProcessModelTypeEnum.ProjReviewPricingApprovalFlow.equals(byName) || ProcessModelTypeEnum.ProjReviewPricingModifyApprovalFlow.equals(byName)) {
//                    // 定价流程详情页展示三份类文件
//                    extQuery.setMaterialsTypes(ProjReviewMaterialsEnum.listPricing());
//                } else {
//                    // 评审流程详情页展示其他文件
//                    extQuery.setMaterialsTypes(ProjReviewMaterialsEnum.listReview());
//                }
//            }
//        }
        return listGroup(req, extQuery);
    }

}

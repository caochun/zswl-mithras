package cn.zswltech.mithras.service.service.assetclassify;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyReviewSubmitREQ;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.common.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.service.enums.assetclassify.AssetClassifyBizNodeEnum;
import cn.zswltech.mithras.service.enums.assetclassify.AssetClassifyInitTypeEnum;
import cn.zswltech.mithras.service.enums.assetclassify.AssetClassifyReviewStatusEnum;
import cn.zswltech.mithras.service.enums.assetclassify.AssetClassifyStatusEnum;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassify;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyClient;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyNodeRecord;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.lib.assetclassify.AssetClassifyLibVersionService;
import cn.zswltech.mithras.service.service.lib.assetclassify.AssetClassifyReviewVersionService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName AssetClassifyVersionService
 * @Description 五级
 * @Author jackerhe
 * @Date 2023/1/5 3:39 下午
 * @Version 1.0
 **/
@Service
public class AssetClassifyVersionService {

    @Resource
    private AssetClassifyClientService assetClassifyClientService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private AssetClassifyReviewVersionService assetClassifyReviewVersionService;
    @Resource
    private AssetClassifyLibVersionService assetClassifyLibVersionService;
    @Resource
    private AssetClassifyNodeRecordService assetClassifyNodeRecordService;
    @Resource
    private AssetClassifyService assetClassifyService;
    @Resource
    private SysUserService sysUserService;

    @Transactional(rollbackFor = Throwable.class)
    public void reviewSubmit(AssetClassifyReviewSubmitREQ req) {
        //检查岗位 资产管理岗
        checkAuth();
        if (!assetClassifyClientService.checkReviewFinish(req.getId())) {
            throw new MithrasException("还有未复核的客户，请完成复核后再提交");
        }
        AssetClassify assetClassify = assetClassifyService.getById(req.getId());
        if (ObjectUtil.isEmpty(assetClassify)) {
            throw new MithrasException("记录不存在！");
        }
        // 增加校验，客户为空，不能发起复核
        List<AssetClassifyClient> ClientList = assetClassifyClientService.list(Wrappers.<AssetClassifyClient>lambdaQuery()
                        .eq(AssetClassifyClient::getAssetClassifyId, req.getId()));
        if (CollectionUtil.isEmpty(ClientList)){
            throw new MithrasException("未存在客户，无法提交数据");
        }
        Map<String, List<AssetClassifyClient>> assetClassifyClientMap = ClientList
                .stream().collect(Collectors.groupingBy(AssetClassifyClient::getReviewStatus));
        if (assetClassifyClientMap.containsKey(AssetClassifyReviewStatusEnum.PROCESS.name())) {
            throw new MithrasException("存在未复合完成的客户，请完成复后再提交！");
        }
        if (isInProcess(req.getId(), BusinessModuleEnum.ASSET_CLASSIFY)) {
            throw new MithrasException("该用户已处于流程中，无法提交数据");
        }
        // 增加校验: 当前整个季中初分流程中，如果复核审批完成了，应当发起下一个评审会而不能重复发起复核
        // 除非是整个流程完成发起下一次季中初分，才可以再次复核
        AssetClassifyNodeRecord node = assetClassifyNodeRecordService.getOne(Wrappers.<AssetClassifyNodeRecord>lambdaQuery()
                .eq(AssetClassifyNodeRecord::getAssetClassifyId, req.getId())
                .eq(AssetClassifyNodeRecord::getNodeName, AssetClassifyBizNodeEnum.REVIEW.name()).last("limit 1"));
        if (ObjectUtil.isNotEmpty(node) && node.getNodeStatue().equals(AssetClassifyStatusEnum.FINISH.name())){
            throw new MithrasException("已完成复核流程，请不要重复提交");
        }
        StartProcessReq startProcessReq = buildReviewSubmitReq(assetClassify);
        startProcessReq.setModelKey(ProcessModelTypeEnum.AssetClassifyReviewFlow.name());
        processApiService.start(startProcessReq);

        // 更新node状态为PROCESS
        AssetClassifyNodeRecord reviewNode = assetClassifyNodeRecordService.getOne(
                Wrappers.<AssetClassifyNodeRecord>lambdaQuery()
                        .eq(AssetClassifyNodeRecord::getAssetClassifyId, req.getId())
                        .eq(AssetClassifyNodeRecord::getNodeName, AssetClassifyBizNodeEnum.REVIEW.name()).last("limit 1"));
        if (ObjectUtil.isNotEmpty(reviewNode)) {
            reviewNode.setNodeStatue(AssetClassifyStatusEnum.PROCESS.name());
            assetClassifyNodeRecordService.updateById(reviewNode);
        }
//        // 记录客户id
//        bizProcessDataService.recordBizData(processInstanceId, assetClassifyClient.getClientId());
//        assetClassifyClientService.updateReviewStatus(req.getId(), AssetClassifyReviewStatusEnum.PROCESS.name());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void directEffect(AssetClassifyReviewSubmitREQ req){
        //检查岗位 资产管理岗
        checkAuth();
        AssetClassifyClient assetClassifyClient = assetClassifyClientService.getById(req.getId());
        if (Objects.isNull(assetClassifyClient)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        checkParam(assetClassifyClient);
        if(AssetClassifyReviewStatusEnum.FINISH.name().equals(assetClassifyClient.getReviewStatus())){
            throw new MithrasException("已生效，请勿重复提交");
        }
        assetClassifyClientService.updateReviewStatus(req.getId(), AssetClassifyReviewStatusEnum.FINISH.name());
        //判断是否已经全部审批
        if(assetClassifyClientService.checkReviewFinish(assetClassifyClient.getAssetClassifyId())){
            //审批完毕，更新节点状态
            assetClassifyNodeRecordService.updateNodeStatue(assetClassifyClient.getAssetClassifyId(), AssetClassifyBizNodeEnum.REVIEW.name(), AssetClassifyStatusEnum.FINISH.name());
            assetClassifyLibVersionService.recordVersion(assetClassifyClient.getAssetClassifyId(), VersionTypeEnum.EFFECT, AccountUtil.getLoginInfo().getId(),
                    null, VersionTypeConstants.NORMAL);
        }
    }

    private void checkParam(AssetClassifyClient assetClassifyClient){
        Assert.isTrue(ObjectUtil.isNotEmpty(assetClassifyClient.getSuggestFlag()), () -> MithrasException.newException("是否调整不能为空"));
    }

    private void checkAuth() {
        Long userId = AccountUtil.getLoginInfo().getId();
        List<String> jobList = sysUserService.queryUserJobList(userId);
        // 资产管理岗可发起
        boolean operation = false;
        for (String jobCode : jobList) {
            if (Objects.equals(JobEnum.assetmanagement.name(), jobCode)) {
                operation = true;
                break;
            }
        }
        Assert.isTrue(operation, () -> MithrasException.newException("仅资产管理岗可操作"));
    }

    private void reviewSubmitCheck(AssetClassifyClient assetClassifyClient){
        if(ObjectUtil.isNull(assetClassifyClient.getSuggestResult())){
            throw new MithrasException("建议分类为空");
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void reviewProcessEnd(ProcessEndContext processEndContext) {
        Long assetClassifyClientId = Long.valueOf(processEndContext.getBusinessKey());
        AssetClassifyClient classifyClient = assetClassifyClientService.getById(assetClassifyClientId);
        boolean processPass = ProcessBusinessStatusEnum.success(processEndContext.getEndType());
        if (processPass) {
            assetClassifyClientService.updateReviewStatus(assetClassifyClientId, AssetClassifyReviewStatusEnum.FINISH.name());
            // 审批通过 新增版本
            assetClassifyReviewVersionService.recordVersion(assetClassifyClientId, VersionTypeEnum.APPROVAL, Long.valueOf(processEndContext.getStartUserId()),
                    processEndContext.getProcessInstanceId(), VersionTypeConstants.NORMAL);
            classifyClient.setReviewPassTime(LocalDateTime.now());
            assetClassifyClientService.updateById(classifyClient);
            //判断是否已经全部审批
//            if(assetClassifyClientService.checkReviewFinish(classifyClient.getAssetClassifyId())){
//                //审批完毕，更新节点状态，生成全流程版本
//                assetClassifyNodeRecordService.updateNodeStatue(classifyClient.getAssetClassifyId(), AssetClassifyBizNodeEnum.REVIEW.name(), AssetClassifyStatusEnum.FINISH.name());
//                assetClassifyLibVersionService.recordVersion(classifyClient.getAssetClassifyId(), VersionTypeEnum.APPROVAL, Long.valueOf(processEndContext.getStartUserId()), processEndContext.getProcessInstanceId(), VersionTypeConstants.NORMAL);
//            }
        } else {
            if (ProcessBusinessStatusEnum.CANCEL.getType().equals(processEndContext.getEndType())) {
                assetClassifyClientService.updateReviewStatus(assetClassifyClientId, AssetClassifyReviewStatusEnum.PROCESS.name());
            }
            if (ProcessBusinessStatusEnum.REJECT.getType().equals(processEndContext.getEndType()) || ProcessBusinessStatusEnum.REJECT_ALL.getType().equals(processEndContext.getEndType())) {
                assetClassifyClientService.updateReviewStatus(assetClassifyClientId, AssetClassifyReviewStatusEnum.PROCESS.name());
            }
            assetClassifyReviewVersionService.recordVersion(assetClassifyClientId, VersionTypeEnum.APPROVAL, Long.valueOf(processEndContext.getStartUserId())
                    , processEndContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
            assetClassifyReviewVersionService.reset(assetClassifyClientId);
        }
    }

    /**
     * 构建通用的启动流程参数
     * 还需自己填充 subModule 和 modelKey
     *
     * @return
     */
    private StartProcessReq buildReviewSubmitReq(AssetClassify assetClassify) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(String.valueOf(assetClassify.getId()));
        // 季中与季末使用不同的流程名称
        if (AssetClassifyInitTypeEnum.QUARTER_MID.name().equals(assetClassify.getInitType())){
            startProcessReq.setProcessInstanceName(String.format("%s年%s季度(季中调整)", assetClassify.getYear(),
                    assetClassify.getQuarter()));
        }else {
            startProcessReq.setProcessInstanceName(String.format("%s年%s季度", assetClassify.getYear(),
                    assetClassify.getQuarter()));
        }
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setVariables(MapUtil.of(
                Pair.of("ccTabShowFlag", Boolean.FALSE)
        ));
        return startProcessReq;
    }


    private boolean isInProcess(Long mainId, BusinessModuleEnum moduleEnum) {
        ProcessPageReq req = new ProcessPageReq();
        req.setBusinessKey(String.valueOf(mainId));
        req.setPageIndex(1);
        req.setPageSize(1);
        req.setModelKeyList(moduleEnum.getModelKeyList());
        req.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        ProcessResp processResp = taskApiService.queryProcess(req).getContents()
                .stream().findFirst().orElse(null);
        return !Objects.isNull(processResp);
    }


}

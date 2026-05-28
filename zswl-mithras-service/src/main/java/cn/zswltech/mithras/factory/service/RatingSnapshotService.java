package cn.zswltech.mithras.factory.service;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.rating.*;
import cn.zswltech.mithras.dto.rating.decision.DecisionExecuteResult;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingCreditMeasureRSP;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingEvaluateBaseRSP;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingQualitativeRSP;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingQuantitativeRSP;
import cn.zswltech.mithras.factory.mapper.RatingReportMapper;
import cn.zswltech.mithras.factory.mapper.RatingSnapshotMapper;
import cn.zswltech.mithras.factory.model.RatingReport;
import cn.zswltech.mithras.factory.model.RatingSnapshot;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RatingSnapshotService extends ServiceImpl<RatingSnapshotMapper, RatingSnapshot> {


    /**
     * 查询评分参数快照-模型
     * @param id
     * @return map: key -> dataType , value -> 对应dataType的所有模型
     */
    public RatingParamInfoRSP getSnapshotInfo(Long id){
        RatingSnapshot ratingSnapshot = this.getById(id);
        if(ratingSnapshot != null) {
            // 取快照中的模板数据
            RatingParamInfoRSP rsp = new RatingParamInfoRSP();
            String content = ratingSnapshot.getContent();
            List<RatingParamFieldRSP> ratingParamFieldRSPS = JSON.parseArray(content, RatingParamFieldRSP.class);
            Map<String, List<RatingParamFieldRSP>> info = ratingParamFieldRSPS.stream().collect(Collectors.groupingBy(RatingParamFieldRSP::getDataType));
            rsp.setInfo(info);
            rsp.setExecuteCount(ratingSnapshot.getExecuteCount());
            return rsp;
        }else{
            return null;
        }
    }

    /**
     * 查询评分参数快照-答题结果
     * @param id
     * @return
     */
    public Map<String, RatingParamRSP> getSnapshotResult(Long id){
        RatingSnapshot decisionParam = this.getById(id);
        if(decisionParam != null) {
            // 取快照中的模板数据
            List<RatingParamRSP> paramList = Optional.ofNullable(JSON.parseArray(decisionParam.getResult(), RatingParamRSP.class)).orElse(new ArrayList<>());
            Map<String, RatingParamRSP> collect = paramList.stream().collect(Collectors.toMap(RatingParamRSP::getFieldName, Function.identity(),(m1, m2)->m2));
            return Optional.of(collect).orElse(new HashMap<>());
        }else{
            return new HashMap<>();
        }
    }

    public Map<String, RatingParamRSP> getSnapshotLastResult(Long id){
        RatingSnapshot decisionParam = this.getById(id);
        if(decisionParam != null) {
            // 取快照中的模板数据
            List<RatingParamRSP> paramList = Optional.ofNullable(JSON.parseArray(decisionParam.getLastResult(), RatingParamRSP.class)).orElse(new ArrayList<>());
            Map<String, RatingParamRSP> collect = paramList.stream().collect(Collectors.toMap(RatingParamRSP::getFieldName, Function.identity(),(m1, m2)->m2));
            return Optional.of(collect).orElse(new HashMap<>());
        }else{
            return new HashMap<>();
        }
    }


    public RatingSnapshotDetailRSP getSnapshotDetail(Long snapshotId){
        RatingSnapshotDetailRSP rsp = new RatingSnapshotDetailRSP(new HashMap<>(), new HashMap<>(), new DecisionExecuteResult());
        RatingSnapshot ratingSnapshot = baseMapper.selectById(snapshotId);
        if(ratingSnapshot.getContent() != null) {
            List<RatingParamFieldRSP> ratingParamFieldRSPS = JSON.parseArray(ratingSnapshot.getContent(), RatingParamFieldRSP.class);
            Map<String, List<RatingParamFieldRSP>> info = ratingParamFieldRSPS.stream().collect(Collectors.groupingBy(RatingParamFieldRSP::getDataType));
            rsp.setContentRsp(info);
        }
        if(ratingSnapshot.getResult() != null){
            List<RatingParamRSP> paramList = JSON.parseArray(ratingSnapshot.getResult(), RatingParamRSP.class);
            Map<String, RatingParamRSP> result = paramList.stream().collect(Collectors.toMap(RatingParamRSP::getFieldName, Function.identity(),(m1, m2)->m2));
            rsp.setResultRsp(result);
        }
        if(ratingSnapshot.getLastResult() != null){
            List<RatingParamRSP> paramList = JSON.parseArray(ratingSnapshot.getLastResult(), RatingParamRSP.class);
            Map<String, RatingParamRSP> result = paramList.stream().collect(Collectors.toMap(RatingParamRSP::getFieldName, Function.identity(),(m1, m2)->m2));
            rsp.setLastResultRsp(result);
        }
        if(ratingSnapshot.getScore() != null){
            DecisionExecuteResult score = JSON.parseObject(ratingSnapshot.getScore(), DecisionExecuteResult.class);
            rsp.setScoreRsp(score);
        }
        rsp.setExecuteCount(ratingSnapshot.getExecuteCount());
        return rsp;
    }


    /**
     * 查询评分参数快照-模型
     * @param id
     * @return map: key -> groupName前的部分 , (key -> groupName后的部分 value -> 对应dataType的所有模型)
     */
    public RatingParamInfoDuoRSP getSnapshotInfoByGroupNameDuo(Long id){
        RatingSnapshot ratingSnapshot = this.getById(id);
        if(ratingSnapshot != null) {
            // 取快照中的模板数据
            RatingParamInfoDuoRSP rsp = new RatingParamInfoDuoRSP();
            String content = ratingSnapshot.getContent();
            List<RatingParamFieldRSP> ratingParamFieldRSPS = JSON.parseArray(content, RatingParamFieldRSP.class);
            Map<String, Map<String, List<RatingParamFieldRSP>>> info = ratingParamFieldRSPS.stream()
                    .filter(f -> f.getGroupName() != null && f.getGroupName().contains("-"))
                    .collect(Collectors.groupingBy(
                            obj -> obj.getGroupName().split("-")[0], // 根据'-'前的部分分组
                            Collectors.groupingBy(
                                    obj -> obj.getGroupName().split("-")[1] // 在每个子组里根据'-'后的部分再分组
                            )));
            rsp.setInfo(info);
            rsp.setExecuteCount(ratingSnapshot.getExecuteCount());
            return rsp;
        }else{
            return null;
        }
    }

    public RatingSnapshotDetailDuoRSP getSnapshotDuoDetail(Long snapshotId){
        RatingSnapshotDetailDuoRSP rsp = new RatingSnapshotDetailDuoRSP(new HashMap<>(), new HashMap<>(), new DecisionExecuteResult());
        RatingSnapshot ratingSnapshot = baseMapper.selectById(snapshotId);
        if(ratingSnapshot.getContent() != null) {
            List<RatingParamFieldRSP> ratingParamFieldRSPS = JSON.parseArray(ratingSnapshot.getContent(), RatingParamFieldRSP.class);
            Map<String, Map<String, List<RatingParamFieldRSP>>> info = ratingParamFieldRSPS.stream()
                    .filter(f -> f.getGroupName() != null && f.getGroupName().contains("-"))
                    .collect(Collectors.groupingBy(
                            obj -> obj.getGroupName().split("-")[0], // 根据'-'前的部分分组
                            Collectors.groupingBy(
                                    obj -> obj.getGroupName().split("-")[1] // 在每个子组里根据'-'后的部分再分组
                            )));
            rsp.setContentRsp(info);
            rsp.setContentList(ratingParamFieldRSPS);
        }
        if(ratingSnapshot.getResult() != null){
            List<RatingParamRSP> paramList = JSON.parseArray(ratingSnapshot.getResult(), RatingParamRSP.class);
            Map<String, RatingParamRSP> result = paramList.stream().collect(Collectors.toMap(RatingParamRSP::getFieldName, Function.identity(),(m1, m2)->m2));
            rsp.setResultRsp(result);
        }
        if(ratingSnapshot.getScore() != null){
            DecisionExecuteResult score = JSON.parseObject(ratingSnapshot.getScore(), DecisionExecuteResult.class);
            rsp.setScoreRsp(score);
        }
        rsp.setExecuteCount(ratingSnapshot.getExecuteCount());
        return rsp;
    }

    /**
     * 查询评分参数快照-模型
     * @param id
     * @param ratingReport
     * @return map: key -> groupName前的部分 , (key -> groupName后的部分 value -> 对应dataType的所有模型)
     */
    public RatingParamInfoDuoApprovalRSP getSnapshotInfoByGroupNameDuoApproval(Long id, RatingReport ratingReport){
        RatingSnapshot ratingSnapshot = this.getById(id);
        if(ratingSnapshot != null) {
            // 取快照中的模板数据
            RatingParamInfoDuoApprovalRSP rsp = new RatingParamInfoDuoApprovalRSP();
            String content = ratingSnapshot.getContent();
            List<RatingParamFieldApprovalRSP> ratingParamFieldRSPS = JSON.parseArray(content, RatingParamFieldApprovalRSP.class);

            // 补充指标审批意见
            if(ratingReport != null && ratingReport.getApprovalInfo() != null) {
                List<RatingReportApprovalRSP.RatingApprovalRSP> approvalInfoList = JSON.parseArray(ratingReport.getApprovalInfo(), RatingReportApprovalRSP.RatingApprovalRSP.class);
                Map<String, RatingReportApprovalRSP.RatingApprovalRSP> approvalInfoMap = approvalInfoList.stream().collect(Collectors.toMap(RatingReportApprovalRSP.RatingApprovalRSP::getFieldName, Function.identity(), (m1, m2) -> m2));
                for (RatingParamFieldApprovalRSP ratingParamFieldRSP : ratingParamFieldRSPS) {
                    RatingReportApprovalRSP.RatingApprovalRSP approvalInfo = approvalInfoMap.getOrDefault(ratingParamFieldRSP.getFieldName(), new RatingReportApprovalRSP.RatingApprovalRSP());
                    ratingParamFieldRSP.setApprovalStatus(approvalInfo.getApprovalStatus());
                    ratingParamFieldRSP.setApprovalOpinion(approvalInfo.getApprovalOpinion());
                }
            }
            Map<String, Map<String, List<RatingParamFieldApprovalRSP>>> info = ratingParamFieldRSPS.stream()
                    .filter(f -> f.getGroupName() != null && f.getGroupName().contains("-"))
                    .collect(Collectors.groupingBy(
                            obj -> obj.getGroupName().split("-")[0], // 根据'-'前的部分分组
                            Collectors.groupingBy(
                                    obj -> obj.getGroupName().split("-")[1] // 在每个子组里根据'-'后的部分再分组
                            )));
            rsp.setInfo(info);
            rsp.setExecuteCount(ratingSnapshot.getExecuteCount());
            return rsp;
        }else{
            return null;
        }
    }


    /**
     * 查询评分参数快照-模型
     * @param id
     * @return map: key -> groupName前的部分 , (key -> groupName后的部分 value -> 对应dataType的所有模型)
     */
    public RatingParamInfoRSP getSnapshotInfoByGroupName(Long id){
        RatingSnapshot ratingSnapshot = this.getById(id);
        if(ratingSnapshot != null) {
            // 取快照中的模板数据
            RatingParamInfoRSP rsp = new RatingParamInfoRSP();
            String content = ratingSnapshot.getContent();
            List<RatingParamFieldRSP> ratingParamFieldRSPS = JSON.parseArray(content, RatingParamFieldRSP.class);
            Map<String, List<RatingParamFieldRSP>> collect = ratingParamFieldRSPS.stream()
                    .collect(Collectors.groupingBy(RatingParamFieldRSP::getGroupName));
            rsp.setInfo(collect);
            rsp.setExecuteCount(ratingSnapshot.getExecuteCount());
            return rsp;
        }else{
            return null;
        }
    }


    /**
     * 判断是否被修改
     * @param snapshotId
     * @param qualitativeRSPS 定性
     * @param quantitativeRSPS 定量
     * @param adjustRSPS 调整事项
     */
    public void compareReport(Long snapshotId,List<RatingQualitativeRSP> qualitativeRSPS,List<RatingQuantitativeRSP> quantitativeRSPS,List<RatingQualitativeRSP> adjustRSPS){
        Map<String, RatingParamRSP> snapshotResult = getSnapshotLastResult(snapshotId);
        if(CollectionUtils.isEmpty(snapshotResult)){
            // 适配已有数据
            return;
        }
        if(CollectionUtils.isNotEmpty(qualitativeRSPS)){
            for (RatingQualitativeRSP qualitativeRSP : qualitativeRSPS) {
                if(!isEqual(snapshotResult.get(qualitativeRSP.getFieldName()),qualitativeRSP.getValue())){
                    qualitativeRSP.setIsChange(true);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(quantitativeRSPS)) {
            for (RatingQuantitativeRSP quantitativeRSP : quantitativeRSPS) {
                if (!isEqual(snapshotResult.get(quantitativeRSP.getFieldName()), quantitativeRSP.getValue())) {
                    quantitativeRSP.setIsChange(true);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(adjustRSPS)) {
            for (RatingQualitativeRSP adjustRSP : adjustRSPS) {
                if (!isEqual(snapshotResult.get(adjustRSP.getFieldName()), adjustRSP.getValue())) {
                    adjustRSP.setIsChange(true);
                }
            }
        }
    }

    /**
     * 判断数据是否被修改
     * @param snapshotId
     * @param evaluateBaseRSPS 评估基准
     * @param creditMeasureRSPS 增信措施
     */
    public void compareReport(Long snapshotId,Map<String, List<RatingEvaluateBaseRSP>> evaluateBaseRSPS, Map<String, List<RatingCreditMeasureRSP>> creditMeasureRSPS){
        Map<String, RatingParamRSP> snapshotResult = getSnapshotResult(snapshotId);
        if(CollectionUtils.isNotEmpty(evaluateBaseRSPS)){
            evaluateBaseRSPS.forEach((k,v) -> {
                for (RatingEvaluateBaseRSP evaluateBaseRSP : v) {
                    if(!isEqual(snapshotResult.get(evaluateBaseRSP.getFieldName()),evaluateBaseRSP.getValue())){
                        evaluateBaseRSP.setIsChange(true);
                    }
                }
            });

        }
        if(CollectionUtils.isNotEmpty(creditMeasureRSPS)) {
            creditMeasureRSPS.forEach((k,v) -> {
                for (RatingCreditMeasureRSP creditMeasureRSP : v) {
                    if (!isEqual(snapshotResult.get(creditMeasureRSP.getFieldName()), creditMeasureRSP.getValue())) {
                        creditMeasureRSP.setIsChange(true);
                    }
                }
            });

        }
    }


    public boolean isEqual(RatingParamRSP ratingParamRSP,Object value){
        if(ratingParamRSP == null){
            return value == null;
        }else{
            return Objects.equals(ratingParamRSP.getFieldValue(),value);
        }
    }
}

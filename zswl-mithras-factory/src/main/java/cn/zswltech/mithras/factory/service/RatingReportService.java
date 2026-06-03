package cn.zswltech.mithras.factory.service;

import cn.zswltech.mithras.dto.rating.*;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingQualitativeRSP;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingQuantitativeRSP;
import cn.zswltech.mithras.factory.mapper.RatingReportMapper;
import cn.zswltech.mithras.factory.model.RatingAmount;
import cn.zswltech.mithras.factory.model.RatingClient;
import cn.zswltech.mithras.factory.model.RatingReport;
import cn.zswltech.mithras.service.others.MithrasException;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RatingReportService extends ServiceImpl<RatingReportMapper, RatingReport> {
    @Resource
    private RatingSnapshotService ratingSnapshotService;

    public void checkApprovalOpinion(RatingClient ratingClient) {
        RatingReport ratingReport = baseMapper.selectById(ratingClient.getReportId());
        if(ratingReport.getApprovalInfo() != null){
            List<RatingReportApprovalRSP.RatingApprovalRSP> approvalRSPList = JSON.parseArray(ratingReport.getApprovalInfo(), RatingReportApprovalRSP.RatingApprovalRSP.class);
//            Map<String, RatingParamRSP> snapshotResult = ratingSnapshotService.getSnapshotResult(ratingClient.getSnapshotId());
            Collection<String> paramIndexKey = getParamIndexKey(ratingClient.getSnapshotId());
            List<String> passFieldList = approvalRSPList.stream().filter(RatingReportApprovalRSP.RatingApprovalRSP::getApprovalStatus).map(RatingReportApprovalRSP.RatingApprovalRSP::getFieldName).collect(Collectors.toList());
            if(!passFieldList.containsAll(paramIndexKey)){
                throw new MithrasException("审核意见中存在不通过指标，不可进行评级下迁/通过");
            }
        }else{
            throw new MithrasException("指标缺少审批,不可提交");
        }
    }

    public void removeApprovalStatus(RatingClient ratingClient){
        RatingReport ratingReport = baseMapper.selectById(ratingClient.getReportId());
        if(ratingReport != null) {
            List<RatingQualitativeRSP> qualitativeList = JSON.parseArray(ratingReport.getQualitative(), RatingQualitativeRSP.class);
            List<RatingQuantitativeRSP> quantitativeList = JSON.parseArray(ratingReport.getQuantitative(), RatingQuantitativeRSP.class);
            List<RatingQualitativeRSP> adjustEventIndex = JSON.parseArray(ratingReport.getAdjustEvent(), RatingQualitativeRSP.class);
            // 流程为退回后重新发起：补充比较字段
            ratingSnapshotService.compareReport(ratingClient.getSnapshotId(), qualitativeList, quantitativeList, adjustEventIndex);
            List<String> qualitativeChangeList = qualitativeList.stream().filter(RatingQualitativeRSP::getIsChange).map(RatingQualitativeRSP::getFieldName).collect(Collectors.toList());
            List<String> quantitativeChangeList = quantitativeList.stream().filter(RatingQuantitativeRSP::getIsChange).map(RatingQuantitativeRSP::getFieldName).collect(Collectors.toList());
            List<String> adjustEventChangeList = adjustEventIndex.stream().filter(RatingQualitativeRSP::getIsChange).map(RatingQualitativeRSP::getFieldName).collect(Collectors.toList());


            if(ratingReport.getApprovalInfo() != null){
                List<RatingReportApprovalRSP.RatingApprovalRSP> approvalRSPList = JSON.parseArray(ratingReport.getApprovalInfo(), RatingReportApprovalRSP.RatingApprovalRSP.class);
                for (RatingReportApprovalRSP.RatingApprovalRSP approvalRSP : approvalRSPList) {
                    String fieldName = approvalRSP.getFieldName();
                    if(qualitativeChangeList.contains(fieldName) || quantitativeChangeList.contains(fieldName) || adjustEventChangeList.contains(fieldName)) {
                        approvalRSP.setApprovalStatus(null);
                    }
                }
                ratingReport.setApprovalInfo(JSON.toJSONString(approvalRSPList));
                baseMapper.updateById(ratingReport);
            }

        }

    }


    public void checkApprovalOpinion(RatingAmount ratingAmount) {
        RatingReport ratingReport = baseMapper.selectById(ratingAmount.getReportId());
        if(ratingReport.getApprovalInfo() != null){
            List<RatingReportApprovalRSP.RatingApprovalRSP> approvalRSPList = JSON.parseArray(ratingReport.getApprovalInfo(), RatingReportApprovalRSP.RatingApprovalRSP.class);
            Map<String, RatingParamRSP> snapshotResult = ratingSnapshotService.getSnapshotResult(ratingAmount.getSnapshotId());
//            Collection<String> paramIndexKey = getParamIndexKey(ratingAmount.getSnapshotId());
            List<String> passFieldList = approvalRSPList.stream().filter(RatingReportApprovalRSP.RatingApprovalRSP::getApprovalStatus).map(RatingReportApprovalRSP.RatingApprovalRSP::getFieldName).collect(Collectors.toList());
            if(!passFieldList.containsAll(snapshotResult.keySet())){
                throw new MithrasException("审核意见中存在不通过指标，不可通过");
            }
        }else{
            throw new MithrasException("指标缺少审批,不可提交");
        }
    }

    private Collection<String> getParamIndexKey(Long snapshotId) {
        RatingSnapshotDetailDuoRSP snapshotDetail = ratingSnapshotService.getSnapshotDuoDetail(snapshotId);
        Map<String, Map<String, List<RatingParamFieldRSP>>> contentRsp = snapshotDetail.getContentRsp();
        Map<String, List<RatingParamFieldRSP>> adjustEvent = contentRsp.get("评级调整事项");
        List<String> indexKeyList = snapshotDetail.getContentList().stream().map(RatingParamFieldRSP::getFieldName).collect(Collectors.toList());
        if(adjustEvent != null){
            List<String> adjustKey = adjustEvent.values().stream().flatMap(Collection::stream).map(RatingParamFieldRSP::getFieldName).collect(Collectors.toList());
            List<String> result= indexKeyList;
            result.removeAll(adjustKey);
            result.add(adjustKey.get(0));
            return result;
        }else{
            return indexKeyList;
        }

    }
}

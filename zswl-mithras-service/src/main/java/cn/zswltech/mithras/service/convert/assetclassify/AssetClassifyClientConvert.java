package cn.zswltech.mithras.service.convert.assetclassify;

import cn.zswltech.mithras.dto.assetclassify.AssetClassifyClientListRSP;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyClient;

/**
 * @author dingqi
 * @date 2023/1/5
 * @description
 */
public class AssetClassifyClientConvert {
    public static AssetClassifyClientListRSP toAssetClassifyClientListRSP(AssetClassifyClient assetClassifyClient) {
        AssetClassifyClientListRSP rsp = new AssetClassifyClientListRSP();
        rsp.setId(assetClassifyClient.getId());
        rsp.setAssetClassifyId(assetClassifyClient.getAssetClassifyId());
        rsp.setClientId(assetClassifyClient.getClientId());
        rsp.setClientName(assetClassifyClient.getClientName());
        rsp.setAmount(assetClassifyClient.getAmount());
        rsp.setStockRiskExposure(assetClassifyClient.getStockRiskExposure());
        rsp.setAssetBalance(assetClassifyClient.getAssetBalance());
        rsp.setRemainingTerm(assetClassifyClient.getRemainingTerm());
        rsp.setQualitativeAdjust(assetClassifyClient.getQualitativeAdjust());
        rsp.setLastClassifyResult(assetClassifyClient.getLastClassifyResult());
        rsp.setClassifyResult(assetClassifyClient.getClassifyResult());
        rsp.setInitClassifyResult(assetClassifyClient.getInitClassifyResult());
        rsp.setReviewStatus(assetClassifyClient.getReviewStatus());
        rsp.setReviewPassTime(assetClassifyClient.getReviewPassTime());
        rsp.setBelongDeptId(assetClassifyClient.getBelongDeptId());
        rsp.setBelongSponsorId(assetClassifyClient.getBelongSponsorId());
        rsp.setAwardRatio(assetClassifyClient.getAwardRatio());
        rsp.setSuggestResult(assetClassifyClient.getSuggestResult());
        return rsp;
    }
}

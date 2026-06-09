package cn.zswltech.mithras.assetclassify.application;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.assetclassify.domain.enums.AssetClassifyBizNodeEnum;
import cn.zswltech.mithras.assetclassify.domain.enums.AssetClassifyStatusEnum;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.AssetClassifyMapper;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.AssetClassifyNodeRecordMapper;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassify;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassifyNodeRecord;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyNodeRSP;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/1/3
 * @description
 */
@Service
public class AssetClassifyNodeRecordService extends ServiceImpl<AssetClassifyNodeRecordMapper, AssetClassifyNodeRecord> {

    @Resource
    private AssetClassifyMapper assetClassifyMapper;

    public AssetClassifyNodeRSP gradeProcess(Long assetClassifyId) {
        AssetClassifyNodeRSP assetClassifyNodeRSPS = new AssetClassifyNodeRSP();
        AssetClassify assetClassify = assetClassifyMapper.selectById(assetClassifyId);
        if(Objects.isNull(assetClassify)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        List<AssetClassifyNodeRecord> assetClassifyNodeRecords = baseMapper.selectList(Wrappers.<AssetClassifyNodeRecord>lambdaQuery()
                .eq(AssetClassifyNodeRecord::getAssetClassifyId, assetClassifyId));
        List<AssetClassifyNodeRSP.NodeMessage> nodeMessages = assetClassifyNodeRecords.stream().map(base -> {
            AssetClassifyNodeRSP.NodeMessage nodeMessage = BeanUtil.copyProperties(base, AssetClassifyNodeRSP.NodeMessage.class);
            nodeMessage.setSort(AssetClassifyBizNodeEnum.of(base.getNodeName()).getOrder());
            return nodeMessage;
        }).sorted(Comparator.comparing(AssetClassifyNodeRSP.NodeMessage::getSort)).collect(Collectors.toList());
        assetClassifyNodeRSPS.setAssetClassifyStatus(assetClassify.getFinish());
        assetClassifyNodeRSPS.setNodeMessages(nodeMessages);
        assetClassifyNodeRSPS.setAssetClassifyInitType(assetClassify.getInitType());
        return assetClassifyNodeRSPS;
    }

    public AssetClassifyNodeRecord getSpecificNode(Long assetClassifyId, AssetClassifyBizNodeEnum assetClassifyBizNodeEnum) {
        LambdaQueryWrapper<AssetClassifyNodeRecord> query = Wrappers.lambdaQuery();
        query.eq(AssetClassifyNodeRecord::getAssetClassifyId, assetClassifyId);
        query.eq(AssetClassifyNodeRecord::getNodeName, assetClassifyBizNodeEnum.name());
        return this.getOne(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void updateNodeStatue(Long assetClassifyId, String nodeName, String nodeStatue) {
        LambdaUpdateChainWrapper<AssetClassifyNodeRecord> assetClassifyNodeRecordLambdaUpdateChainWrapper = lambdaUpdate();
        assetClassifyNodeRecordLambdaUpdateChainWrapper.set(AssetClassifyNodeRecord::getNodeStatue, nodeStatue);
        if (Objects.equals(nodeStatue, AssetClassifyStatusEnum.FINISH.name())) {
            // 如果是完成，同时更新结束时间
            assetClassifyNodeRecordLambdaUpdateChainWrapper.set(AssetClassifyNodeRecord::getEndTime, LocalDateTime.now());
        }
        assetClassifyNodeRecordLambdaUpdateChainWrapper.eq(AssetClassifyNodeRecord::getAssetClassifyId, assetClassifyId);
        assetClassifyNodeRecordLambdaUpdateChainWrapper.eq(AssetClassifyNodeRecord::getNodeName, nodeName);
        assetClassifyNodeRecordLambdaUpdateChainWrapper.update();
//        this.update(assetClassifyNodeRecordLambdaUpdateChainWrapper);
    }

}

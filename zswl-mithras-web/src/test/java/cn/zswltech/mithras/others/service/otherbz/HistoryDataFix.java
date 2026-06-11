package cn.zswltech.mithras.others.service.otherbz;

import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewBaseInfoLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfoLib;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 修复历史数据
 *
 * @author wangchuanhao
 * @date 2022/11/1 2:33 PM
 */
public class HistoryDataFix extends ApplicationTest {

    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoLibMapper projReviewBaseInfoLibMapper;

    /**
     * 修复项目评审模块基本信息 保理业务、转让业务 支持多债权人
     */
    @Test
    public void fixProjReviewBaseInfo() {
        List<ProjReviewBaseInfo> projReviewBaseInfoList = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery().in(ProjReviewBaseInfo::getBizType, Arrays.asList(ProjectBizType.BL.name(), ProjectBizType.ZR.name())));
        List<ProjReviewBaseInfoLib> projReviewBaseInfoLibList = projReviewBaseInfoLibMapper.selectList(Wrappers.<ProjReviewBaseInfoLib>lambdaQuery().in(ProjReviewBaseInfoLib::getBizType, Arrays.asList(ProjectBizType.BL.name(), ProjectBizType.ZR.name())));
        projReviewBaseInfoList.forEach(p -> {
            ClientInfo clientInfo = new ClientInfo();
            clientInfo.setClientId(p.getCreditorClientId());
            clientInfo.setStockRiskExposure(p.getCreditorStockRiskExposure());
            p.setCreditorInfo(JSON.toJSONString(Arrays.asList(clientInfo)));
            projReviewBaseInfoMapper.updateById(p);
        });
        projReviewBaseInfoLibList.forEach(p -> {
            ClientInfo clientInfo = new ClientInfo();
            clientInfo.setClientId(p.getCreditorClientId());
            clientInfo.setStockRiskExposure(p.getCreditorStockRiskExposure());
            p.setCreditorInfo(JSON.toJSONString(Arrays.asList(clientInfo)));
            projReviewBaseInfoLibMapper.updateById(p);
        });
    }


}

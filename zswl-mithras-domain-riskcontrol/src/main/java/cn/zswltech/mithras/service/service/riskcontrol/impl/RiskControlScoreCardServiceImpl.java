package cn.zswltech.mithras.service.service.riskcontrol.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlScoreCardTargetListRSP;
import cn.zswltech.mithras.dto.riskcontrol.scorecard.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.convert.riskcontrol.RiskControlCardTargetConverter;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.riskcontrol.AreaTypeEnum;
import cn.zswltech.mithras.service.enums.riskcontrol.RiskControlAssertEnum;
import cn.zswltech.mithras.service.enums.riskcontrol.TitleNameEnum;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlScoreCardBaseInfo;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlScoreCardData;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlScoreCardTarget;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlScoreCardTitle;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.riskcontrol.*;
import cn.zswltech.mithras.service.service.riskcontrol.dto.TryCalculateDto;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @ClassName RiskControlScoreCardServiceImpl
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/2/27 3:37 下午
 * @Version 1.0
 **/
@Service
@Slf4j
public class RiskControlScoreCardServiceImpl implements RiskControlScoreCardService {

    @Resource
    private RiskControlScoreCardTitleService riskControlScoreCardTitleService;
    @Resource
    private RiskControlScoreCardDataService riskControlScoreCardDataService;
    @Resource
    private RiskControlScoreCardTargetService riskControlScoreCardTargetService;
    @Resource
    private RiskControlScoreCardBaseInfoService riskControlScoreCardBaseInfoService;
    @Resource
    private RiskControlCardTargetConverter riskControlCardTargetConverter;

    private final static Set<String> ignoreTargetSet = new HashSet<>();
    static {
        ignoreTargetSet.add("地区");
        ignoreTargetSet.add("省");
        ignoreTargetSet.add("市");
        ignoreTargetSet.add("行政级别");
        ignoreTargetSet.add("区域级别");
    }


    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Throwable.class)
    public void importFile(RiskControlScoreCordImportREQ req) {
        ExcelReader reader = ExcelUtil.getReader(req.getFile().getInputStream());
        List<List<Object>> readLine = reader.read(0, reader.getRowCount());
        if(ObjectUtil.isEmpty(readLine) || readLine.size() < 1){
            return;
        }
        Map<Integer, String> line2Title;
        RiskControlScoreCardTitle scoreCardTitle;
        RiskControlScoreCardData scoreCardData;
        List<Object> row;
        List<RiskControlScoreCardTitle> importTitle = new ArrayList<>();
        List<RiskControlScoreCardData> addData = new ArrayList<>();
        List<Object> zeroRow = readLine.get(0);
        Integer year = req.getYear();
        //第一行认为是标题
        for (int i = 0; i < zeroRow.size(); i++) {
            scoreCardTitle = new RiskControlScoreCardTitle();
            scoreCardTitle.setYear(year);
            scoreCardTitle.setColumnNumber(i);
            scoreCardTitle.setName(String.valueOf(zeroRow.get(i)));
            scoreCardTitle.setTargetType(ignoreTargetSet.contains(scoreCardTitle.getName()) ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode());
            importTitle.add(scoreCardTitle);
        }
        List<RiskControlScoreCardTitle> list = riskControlScoreCardTitleService.list(Wrappers.<RiskControlScoreCardTitle>lambdaQuery()
                .eq(RiskControlScoreCardTitle::getYear, year));
        if(ObjectUtil.isNotEmpty(list)){
            Set<String> oldSet = list.stream().map(RiskControlScoreCardTitle::getName).collect(Collectors.toSet());
            //清理当年旧数据
            riskControlScoreCardDataService.remove(Wrappers.<RiskControlScoreCardData>lambdaQuery()
                    .in(ObjectUtil.isNotEmpty(oldSet), RiskControlScoreCardData::getTitleName, oldSet)
                    .eq(RiskControlScoreCardData::getYear, year));
            //删除标题
            riskControlScoreCardTitleService.remove(Wrappers.<RiskControlScoreCardTitle>lambdaQuery()
                    .eq(RiskControlScoreCardTitle::getYear, year));
        }
        if(ObjectUtil.isNotEmpty(importTitle)){
            riskControlScoreCardTitleService.saveBatch(importTitle, 500);
        }
        //获取行政级别
        RiskControlScoreCardTitle executiveLevel = riskControlScoreCardTitleService.getOne(Wrappers.<RiskControlScoreCardTitle>lambdaQuery()
                .eq(RiskControlScoreCardTitle::getName, TitleNameEnum.REGIONAL_LEVEL.display)
                .eq(RiskControlScoreCardTitle::getYear, year)
                .last(StringUtil.mysqlLimitOne()));

        line2Title = importTitle.stream().collect(Collectors.toMap(RiskControlScoreCardTitle::getColumnNumber, RiskControlScoreCardTitle::getName));
        for(int rowId = 1; rowId < readLine.size(); rowId++){
            row = readLine.get(rowId);
            //处理每一行
            for(int i = 0; i<row.size(); i++ ){
                scoreCardData = new RiskControlScoreCardData();
                if(ObjectUtil.isNotEmpty(executiveLevel) && ObjectUtil.equals(i, executiveLevel.getColumnNumber())){
                    scoreCardData.setContent(AreaTypeEnum.change(String.valueOf(row.get(i))).name());
                }else {
                    scoreCardData.setContent(String.valueOf(row.get(i)));
                }
                scoreCardData.setRow(rowId);
                scoreCardData.setTitleName(line2Title.get(i));
                scoreCardData.setYear(year);
                addData.add(scoreCardData);
            }
        }
        riskControlScoreCardDataService.saveBatch(addData, 500);
    }

    @Override
    public Boolean effectAuth(Long cardId) {
        RiskControlScoreCardBaseInfo baseInfo = riskControlScoreCardBaseInfoService.getById(cardId);
        if(ObjectUtil.isEmpty(baseInfo)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        List<RiskControlScoreCardTarget> riskControlScoreCardTargets = riskControlScoreCardTargetService.list(Wrappers.<RiskControlScoreCardTarget>lambdaQuery()
                .eq(RiskControlScoreCardTarget::getCardId, cardId));
        if(ObjectUtil.isEmpty(riskControlScoreCardTargets)){
           return Boolean.FALSE;
        }
        AtomicReference<Long> sum = new AtomicReference<>(0L);
        riskControlScoreCardTargets.forEach(target -> sum.updateAndGet(v -> v + target.getTargetWeight()));
        if(ObjectUtil.notEqual(sum.get(), 100L)){
            return Boolean.FALSE;
        }
        //baseInfo.setStatus(RiskControlAssertEnum.NOT_EFFECT.name());
        //riskControlScoreCardBaseInfoService.updateById(baseInfo);
        return Boolean.TRUE;
    }

    @Override
    public List<RiskControlScoreCordAreaSearchRSP> areaSearch(RiskControlScoreCordAreaSearchREQ req) {

        List<RiskControlScoreCordAreaSearchRSP> rsps = new ArrayList<>();
        if(ObjectUtil.isEmpty(req.getYear())){
            req.setYear(LocalDate.now().getYear());
        }
        List<RiskControlScoreCardData> list = riskControlScoreCardDataService.list(Wrappers.<RiskControlScoreCardData>lambdaQuery()
                .eq(RiskControlScoreCardData::getTitleName, req.getAreaType())
                .eq(RiskControlScoreCardData::getYear, req.getYear())
                .like(ObjectUtil.isNotEmpty(req.getAreaName()), RiskControlScoreCardData::getContent, req.getAreaName()));
        list.forEach(base -> {
            RiskControlScoreCordAreaSearchRSP rsp = new RiskControlScoreCordAreaSearchRSP();
            rsp.setAreaName(base.getContent());
            rsp.setId(base.getId());
            rsps.add(rsp);
        });
        return rsps;
    }

    @Override
    public List<RiskControlScoreCordAreaAllRSP> areaAll(RiskControlScoreCordAreaSearchREQ req) {
        List<RiskControlScoreCordAreaAllRSP> rsps = new ArrayList<>();
        Map<Integer, RiskControlScoreCardData> provinceMap = riskControlScoreCardDataService.getAreaMap(TitleNameEnum.PROVINCE, req.getYear());
        Map<Integer, RiskControlScoreCardData> cityMap = riskControlScoreCardDataService.getAreaMap(TitleNameEnum.CITY, req.getYear());
        Map<Integer, RiskControlScoreCardData> areaMap = riskControlScoreCardDataService.getAreaMap(TitleNameEnum.AREA, req.getYear());
        Map<String, Map<String, List<RiskControlScoreCordAreaAllRSP>>> pMap = new HashMap<>();

        provinceMap.forEach((pKey, pValue) -> {
            //省
            Map<String, List<RiskControlScoreCordAreaAllRSP>> cMap;
            if(ObjectUtil.isEmpty(pMap.get(pValue.getContent()))){
                /*RiskControlScoreCordAreaSearchRSP pRsp = new RiskControlScoreCordAreaSearchRSP();
                pRsp.setId(pValue.getId());
                pRsp.setAreaType(TitleNameEnum.PROVINCE.name());
                pRsp.setAreaName(pValue.getTitleName());*/
                cMap = new HashMap<>();
                pMap.put(pValue.getContent(), cMap);
            }
            //市
            RiskControlScoreCardData city = cityMap.get(pKey);
            if(ObjectUtil.isEmpty(city)){
                throw new MithrasException("城市信息不存在");
            }
            cMap = pMap.get(pValue.getContent());
            if(ObjectUtil.isEmpty(cMap.get(city.getContent()))){
                cMap.put(city.getContent(), new ArrayList<>());
            }
            RiskControlScoreCardData area = areaMap.get(pKey);
            RiskControlScoreCordAreaAllRSP pRsp = new RiskControlScoreCordAreaAllRSP();
            pRsp.setId(area.getId());
            pRsp.setAreaType(TitleNameEnum.AREA.name());
            pRsp.setAreaName(area.getContent());
            cMap.get(city.getContent()).add(pRsp);
        });
        //补全信息
        pMap.forEach((key, value) -> {
            RiskControlScoreCordAreaAllRSP rsp = new RiskControlScoreCordAreaAllRSP();
            rsp.setAreaType(TitleNameEnum.PROVINCE.name());
            rsp.setAreaName(key);
            List<RiskControlScoreCordAreaAllRSP> cityList = new ArrayList<>();
            value.forEach((key1, value1) -> {
                RiskControlScoreCordAreaAllRSP rsp1 = new RiskControlScoreCordAreaAllRSP();
                rsp1.setAreaType(TitleNameEnum.CITY.name());
                rsp1.setAreaName(key1);
                rsp1.setChild(value1);
                cityList.add(rsp1);
            });
            rsp.setChild(cityList);
            rsps.add(rsp);
        });

        return rsps;
    }


    @Override
    public RiskControlScoreCordTryCalculateRSP tryCalculate(RiskControlScoreCordTryCalculateREQ req) {
        RiskControlScoreCardBaseInfo cardBaseInfo = riskControlScoreCardBaseInfoService.getById(req.getCardId());
        RiskControlScoreCardData areaData = riskControlScoreCardDataService.getById(req.getAreaId());
        if(ObjectUtil.isEmpty(cardBaseInfo) || ObjectUtil.isEmpty(areaData)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //验证生效
        if(ObjectUtil.notEqual(cardBaseInfo.getStatus(), RiskControlAssertEnum.EFFECT.name())){
            throw new MithrasException("评分卡未生效");
        }
        Map<String, String> areaDataByTitleNameEnum = riskControlScoreCardTitleService.getAreaDataByTitleNameEnum(areaData.getRow(), LocalDate.now().getYear());
        RiskControlScoreCordTryCalculateRSP rsp = new RiskControlScoreCordTryCalculateRSP();
        rsp.setAreaId(areaData.getId());
        rsp.setCardId(req.getCardId());
        rsp.setArea(areaDataByTitleNameEnum.get(TitleNameEnum.AREA.display));
        rsp.setProvince(areaDataByTitleNameEnum.get(TitleNameEnum.PROVINCE.display));
        rsp.setCity(areaDataByTitleNameEnum.get(TitleNameEnum.CITY.display));
        rsp.setExecutiveLevel(areaDataByTitleNameEnum.get(TitleNameEnum.EXECUTIVE_LEVEL.display));
        rsp.setRegionalLevel(areaDataByTitleNameEnum.get(TitleNameEnum.REGIONAL_LEVEL.display));
        //查找指标
        List<RiskControlScoreCardTarget> targets = riskControlScoreCardTargetService.list(Wrappers.<RiskControlScoreCardTarget>lambdaQuery()
                .eq(RiskControlScoreCardTarget::getCardId, req.getCardId()));
        if(ObjectUtil.isEmpty(targets)){
            throw new MithrasException("评分卡无指标信息");
        }
        List<RiskControlScoreCardTargetListRSP> targetRsp = targets.stream().map(riskControlCardTargetConverter::entity2Rsp).collect(Collectors.toList());
        //查询地区信息
        List<String> targetNames = targets.stream().map(RiskControlScoreCardTarget::getTargetName).collect(Collectors.toList());
        List<RiskControlScoreCardData> dataList = riskControlScoreCardDataService.list(Wrappers.<RiskControlScoreCardData>lambdaQuery()
                .eq(RiskControlScoreCardData::getRow, areaData.getRow())
                .eq(RiskControlScoreCardData::getYear, ObjectUtil.isNotEmpty(cardBaseInfo.getYear()) ? cardBaseInfo.getYear() : LocalDate.now().getYear())
                .in(RiskControlScoreCardData::getTitleName, targetNames));

        TryCalculateDto tryCalculateDto = new TryCalculateDto(targetRsp, dataList);
        tryCalculateDto.calculate(rsp);
        if(ObjectUtil.isNotEmpty(rsp.getTryCalculateBodies())){
            rsp.setTotalPoints(new BigDecimal(0));
            rsp.getTryCalculateBodies().forEach(base -> rsp.setTotalPoints(rsp.getTotalPoints().add(Optional.ofNullable(base.getScore()).orElse(new BigDecimal(0)))));
        }

        return rsp;
    }
}

package cn.zswltech.mithras.blackgray.providence.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.blackgray.dto.req.CompleteWarehouseREQ;
import cn.zswltech.mithras.blackgray.enums.BlackGrayOrgEnum;
import cn.zswltech.mithras.blackgray.enums.BlackGraySourceEnum;
import cn.zswltech.mithras.blackgray.enums.BlackGrayTypeEnum;
import cn.zswltech.mithras.blackgray.service.BlackGrayLibraryService;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.third.providence.entity.BillOverdue;
import cn.zswltech.mithras.third.providence.mapper.BillOverdueMapper;
import cn.zswltech.mithras.third.providence.req.BillOverdueReq;
import cn.zswltech.mithras.third.providence.rsp.BillOverdueRsp;
import cn.zswltech.mithras.third.providence.service.BillService;
import cn.zswltech.mithras.third.providence.util.BillOverduePdfParser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.zswltec.providence.dto.base.PageR;
import com.zswltec.providence.dto.base.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jim
 * @version 1.0.0
 * @descripition:
 * @date 2024/12/24 14:21
 */
@Service
@Slf4j
public class BillServiceImpl implements BillService {

    @Resource
    private BillOverdueMapper billOverdueMapper;

    @Resource
    private BlackGrayLibraryService blackGrayLibraryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R overdueListImport(MultipartFile multipartFile, String busiDate) {
        List<BillOverdue> billOverdueList = BillOverduePdfParser.parse(multipartFile, busiDate);
        SpringUtil.getBean(BillService.class).refreshData(billOverdueList, busiDate);
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshData(List<BillOverdue> billOverdueList, String busiDate) {
        billOverdueMapper.delete(new LambdaQueryWrapper<BillOverdue>().eq(BillOverdue::getBusiDate,busiDate));
        List<List<BillOverdue>> partition = Lists.partition(billOverdueList, 200);
        for (List<BillOverdue> billOverdues : partition) {
            billOverdueMapper.batchSave(billOverdues);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = null;
        try {
            parse = sdf.parse(busiDate);
        } catch (ParseException e) {
            log.error("overdueListImport busiDate error {}", busiDate);
            throw new MithrasException("时间节点异常");
        }
        // 刷新黑灰名单
        // TODO
        Date finalParse = parse;
        List<CompleteWarehouseREQ> warehouseReq = billOverdueList.stream().map(billOverdue -> {
            CompleteWarehouseREQ req = new CompleteWarehouseREQ();
            req.setEnterpriseName(billOverdue.getOrgName());
            req.setUnifiedSocialCreditCode(billOverdue.getOrgCode());
            req.setWarehouseTime(finalParse);
            req.setShareType(YesOrNoNumberEnum.NO.getCode());
            req.setBlackGrayType(BlackGrayTypeEnum.GRAY_LIST.name());
            req.setBlackGrayTypeNum(BlackGrayTypeEnum.GRAY_LIST.getNum());
            req.setBlackGraySort(40);
            req.setApplyOrganization(BlackGrayOrgEnum.ZSZL.name());
            req.setShareType(YesOrNoNumberEnum.NO.getCode());
            req.setSource(BlackGraySourceEnum.INTERNAL_UPLOAD.name());
            return req;
        }).collect(Collectors.toList());
        blackGrayLibraryService.completeWarehouse(warehouseReq,"IG20250114000032");
    }

    @Override
    public PageR<BillOverdueRsp> overdueList(BillOverdueReq req) {
        Page<BillOverdue> page = new Page<>(req.getPage(),req.getPageSize());
        String busiDate = req.getBusiDate();
        LambdaQueryWrapper<BillOverdue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(BillOverdue::getBusiDate);
        if (ObjectUtil.isNotNull(busiDate)){
            queryWrapper.eq(BillOverdue::getBusiDate, busiDate);
        }
        if (ObjectUtil.isNotNull(req.getOrgName())){
            queryWrapper.like(BillOverdue::getOrgName, req.getOrgName());
        }

        Page<BillOverdue> overduePage = billOverdueMapper.selectPage(page, queryWrapper);

        return PageR.of(BeanUtil.copyToList(overduePage.getRecords(), BillOverdueRsp.class), overduePage.getTotal(), overduePage.getPages(), req.getPage(), req.getPageSize());
    }

    @Override
    public List<BillOverdue> latestOverdueList(String busiDate) {
        if (ObjectUtil.isNull(busiDate)){
            busiDate = billOverdueMapper.selectLatestBusiDate();
        }
        return billOverdueMapper.selectList(new LambdaQueryWrapper<BillOverdue>().eq(BillOverdue::getBusiDate, busiDate));
    }

}

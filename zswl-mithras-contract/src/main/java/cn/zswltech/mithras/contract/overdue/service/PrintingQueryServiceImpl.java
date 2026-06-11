package cn.zswltech.mithras.contract.overdue.service;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.contract.overdue.application.assembler.DocPrintingAssembler;
import cn.zswltech.mithras.contract.overdue.application.dto.PrintingListDto;
import cn.zswltech.mithras.contract.overdue.application.query.PrintingPageQuery;
import cn.zswltech.mithras.contract.overdue.application.docprinting.PrintingQueryService;
import cn.zswltech.mithras.contract.overdue.dao.DocPrintingDao;
import cn.zswltech.mithras.contract.overdue.model.DocPrinting;
import cn.zswltech.mithras.foundation.port.ProcessStartUserResolver;
import cn.zswltech.mithras.foundation.port.UserBizDeptResolver;
import cn.zswltech.mithras.foundation.port.UserNameResolver;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/5 15:14
 */
@Service
public class PrintingQueryServiceImpl implements PrintingQueryService {
    @Resource
    private DocPrintingDao docPrintingDao;
    @Resource
    private DocPrintingAssembler docPrintingAssembler;
    @Resource
    private UserNameResolver userNameResolver;
    @Resource
    private UserBizDeptResolver userBizDeptResolver;
    @Resource
    private ProcessStartUserResolver processStartUserResolver;

    @Override
    public PageR<PrintingListDto> page(PrintingPageQuery query) {
        Page<DocPrinting> page = docPrintingDao.page(new Page<>(query.getPage(), query.getPageSize()),
                Wrappers.<DocPrinting>lambdaQuery()
                        .eq(ObjectUtil.isNotEmpty(query.getApplicant()), DocPrinting::getCreateBy, query.getApplicant())
                        .eq(ObjectUtil.isNotEmpty(query.getType()), DocPrinting::getType, query.getType())
                        .orderByDesc(DocPrinting::getUpdateTime));
        List<PrintingListDto> printingListDto = docPrintingAssembler.po2ListDto(page.getRecords());
        Map<Long, String> userNames = userNameResolver.sysUserId2Name(printingListDto.stream().map(PrintingListDto::getCreateBy).collect(Collectors.toSet()));
        for (PrintingListDto item : printingListDto) {
            item.setCreateByName(userNames.get(item.getCreateBy()));
            if (StringUtils.isNotBlank(item.getProcessId())) {
                Long startUserId = processStartUserResolver.processStartUserId(item.getProcessId());
                if (startUserId != null) {
                    item.setApplyDeptName(userBizDeptResolver.getBizDeptNameByUserId(startUserId));
                    item.setApplyName(userNameResolver.sysUserId2NameSingle(startUserId));
                }
            } else {
                item.setProcessStatus("UN_SUBMIT");
            }
        }
        return PageR.of(page, printingListDto);
    }
}

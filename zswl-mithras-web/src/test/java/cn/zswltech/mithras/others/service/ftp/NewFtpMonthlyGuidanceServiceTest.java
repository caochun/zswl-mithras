package cn.zswltech.mithras.others.service.ftp;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayLibraryREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayLibraryRSP;
import cn.zswltech.mithras.blackgray.service.BlackGrayLibraryService;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.customer.enums.client.ClientStatus;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.ftp.newftp.service.NewFtpBaseInfoService;
import cn.zswltech.mithras.ftp.newftp.service.draft.NewFtpMonthlyGuidanceDraftService;
import cn.zswltech.mithras.ftp.newftp.service.draft.NewFtpQuarterlyBasePricingDraftService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.*;

import static cn.hutool.core.util.ObjectUtil.isNull;

/**
 * @author dingqi
 * @date 2023/11/20
 * @description
 */
public class NewFtpMonthlyGuidanceServiceTest extends ApplicationTest {
    @Resource
    private NewFtpMonthlyGuidanceDraftService newFtpMonthlyGuidanceService;
    @Resource
    private FundDirectFinancingBaseInfoService directFinancingBaseInfoService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private BlackGrayLibraryService blackGrayLibraryService;

    @Test
    public void quarterPricingsTest() {
//        LocalDate thisMonth = LocalDate.of(2023, 11, 1);
//        LocalDate[] lastQuarterMonths = new LocalDate[]{
//                thisMonth.minusMonths(3),
//                thisMonth.minusMonths(2),
//                thisMonth.minusMonths(1)
//        };
//        List<NewFtpQuarterPricingBO> result = newFtpMonthlyGuidanceService.quarterPricings(Arrays.asList(lastQuarterMonths));
//        System.out.println(JSONUtil.toJsonStr(result));
        NewFtpBaseInfo newFtpBaseInfo = SpringUtil.getBean(NewFtpBaseInfoService.class).getById(453);
        SpringUtil.getBean(NewFtpQuarterlyBasePricingDraftService.class).calculate(newFtpBaseInfo);
    }

    @Test
    public void monthlyGuidanceAddTest() throws InterruptedException {
//        directFinancingBaseInfoService.updateFinancingCost(251014l);
//        251014l
    }


    @Test
    public void libraryRecordTest(){

        List<Client> clientList = SpringContextHolder.getBean(ClientMapper.class).selectList(Wrappers.<Client>lambdaQuery().eq(Client::getClientType, ClientType.CORPORATION.name()).orderByAsc(Client::getId));
        Set<Long> creatorIdList = new HashSet<>(clientList.size());
        Set<Long> deptIdList = new HashSet<>(clientList.size() / 2);
        clientList.forEach(e -> {
            creatorIdList.add(e.getBelongSponsorId());
            deptIdList.add((e.getBelongDeptId()));
        });
        Map<Long, String> nameMap = id2NameService.sysUserId2Name(creatorIdList);
        Map<Long, String> orgNameMap = id2NameService.deptId2Name(deptIdList);


        List<List<Object>> resultDataList = new LinkedList<>();
        for (Client client : clientList) {
            BlackGrayLibraryREQ req = new BlackGrayLibraryREQ();
            req.setClientId(client.getId());
            BlackGrayLibraryRSP rsp =blackGrayLibraryService.libraryRecord(req);
            if(rsp != null && StringUtils.isNotBlank(rsp.getApplyReason())) {
                resultDataList.add(ListUtil.of(
                        client.getId(),
                        client.getClientName(),
                        client.getUscCode(),
                        nameMap.get(client.getBelongSponsorId()) != null ? nameMap.get(client.getBelongSponsorId()) : "",
                        orgNameMap.get(client.getBelongDeptId()) != null ? orgNameMap.get(client.getBelongDeptId()) : "",
                        rsp.getApplyReason(),
                        rsp.getWarehouseTime(),
                        clientStatusDisplay(client),
                        rsp.getBlackGrayType()
                ));
            }
        }
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        excelWriter.writeHeadRow(ListUtil.of("客户ID", "客户名称", "统一社会信用代码","所属主办", "所属部门", "入库原因","入口时间","客户状态","黑灰名单"));
        for (List<Object> row : resultDataList) {
            excelWriter.writeRow(row);
        }
        excelWriter.flush(FileUtil.getOutputStream("F:/gray/客户灰名单.xlsx"), true);


//        BlackGrayLibraryREQ req = new BlackGrayLibraryREQ();
//        req.setClientId(3809l);
//        BlackGrayLibraryRSP rsp =blackGrayLibraryService.libraryRecord(req);
//        System.out.println(rsp);
//
//        req.setClientId(6927l);
//        rsp =blackGrayLibraryService.libraryRecord(req);
//        System.out.println(rsp);

        System.out.println("-----------------------");
    }

    private String clientStatusDisplay(Client c) {
        if (Objects.equals(c.getClientStatus(), ClientStatus.NEW.name()) && Objects.equals(c.getIsReleased(), YesOrNoNumberEnum.YES.getCode())) {
            return ClientStatus.RELEASE.display;
        }
        return isNull(ClientStatus.of(c.getClientStatus())) ? c.getClientStatus() : ClientStatus.of(c.getClientStatus()).display;
    }

}

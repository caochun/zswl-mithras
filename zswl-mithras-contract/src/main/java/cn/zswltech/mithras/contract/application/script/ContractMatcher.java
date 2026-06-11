package cn.zswltech.mithras.contract.application.script;

import cn.zswltech.mithras.contract.excel.exporter.ContractMatcherResultExporter;
import cn.zswltech.mithras.contract.excel.model.ContractMatcherResultModel;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/6 15:33
 */
@Service
public class ContractMatcher {
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ContractMatcherResultExporter exporter;

    public void match(InputStream inputStream, OutputStream out) throws IOException {
        Map<String, ContractBaseInfo> codeToBaseInfo = contractBaseInfoService.getBaseMapper().selectList(
                Wrappers.<ContractBaseInfo>lambdaQuery().orderByDesc(BaseModel::getUpdateTime))
                .stream().filter(distinctByField(ContractBaseInfo::getContractCode)).collect(Collectors.toMap(ContractBaseInfo::getContractCode, item->item));
        Map<Long, String> clientId2Name = clientMapper.selectBatchIds(codeToBaseInfo.values().stream()
                        .map(ContractBaseInfo::getClientId)
                        .distinct()
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(Client::getId, Client::getClientName, (a, b) -> a));
        XSSFWorkbook sheets = new XSSFWorkbook(inputStream);
        List<ContractMatcherResultModel> errorContract = new ArrayList<>();
        Sheet sheet = sheets.getSheetAt(0);
        Row row = sheet.getRow(3);
        while(!row.getCell(0).getStringCellValue().equals("")){
            String contractCode = row.getCell(2).getStringCellValue();
            String clientName = row.getCell(11).getStringCellValue();
            ContractMatcherResultModel model = new ContractMatcherResultModel();
            model.setContractCode(contractCode);
            model.setDeptName(row.getCell(0).getStringCellValue());
            model.setProjSponsorUserName(row.getCell(1).getStringCellValue());
            model.setClientName(clientName);
            if(!codeToBaseInfo.containsKey(contractCode)){
                model.setReason("合同未录入系统");
                errorContract.add(model);
            }else {
                ContractBaseInfo contractBaseInfo = codeToBaseInfo.get(contractCode);
                model.setSysContractCode(contractBaseInfo.getContractCode());
                Long clientId = contractBaseInfo.getClientId();
                if(clientName.length() < 5){
                    if(!clientId2Name.get(clientId).equals(clientName)){
                        model.setReason("客户名称与系统不符");
                        model.setSysClientName(clientId2Name.get(clientId));
                        errorContract.add(model);
                    }
                }else {
                    if(!clientId2Name.get(clientId).substring(0,4).equals(clientName.substring(0,4))){
                        model.setReason("客户名称与系统不符");
                        model.setSysClientName(clientId2Name.get(clientId));
                        errorContract.add(model);
                    }
                }
            }
            row = sheet.getRow(row.getRowNum()+1);
        }
        exporter.exportExcel(errorContract, out);
    }

    static <T> Predicate<T> distinctByField(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}

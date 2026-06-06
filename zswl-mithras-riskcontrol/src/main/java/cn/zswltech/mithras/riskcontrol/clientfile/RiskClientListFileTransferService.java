package cn.zswltech.mithras.riskcontrol.clientfile;

import java.time.LocalDate;
import java.util.List;

/**
 * 风控客户沙盘名单文件生成与传输端口。
 */
public interface RiskClientListFileTransferService {

    List<RiskClientListFileDTO> getClientList(LocalDate today);

    <T> void createAndTransferCsv(String fileName, List<T> dataList, String path) throws Exception;
}

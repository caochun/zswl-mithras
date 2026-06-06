package cn.zswltech.mithras.service.service.document.job;

import cn.zswltech.mithras.document.application.job.FileTemplateKeyInitJobService;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import cn.zswltech.mithras.service.util.ChineseToPinyinUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @date 2025/1/9 18:15
 * @description
 */
@Component
@Slf4j
public class FileTemplateKeyInitJobServiceImpl implements FileTemplateKeyInitJobService {

    @Resource
    private FileTemplateService fileTemplateService;

    // 初始化文件模版的Key 只能用一次
    @Override
    public void init() {
        log.info("FileTemplateKeyInitJob init begin..........");
        List<FileTemplate> effectList = fileTemplateService.list(Wrappers.<FileTemplate>lambdaQuery()
                .eq(FileTemplate::getOutdated, 0));
        List<FileTemplate> needUpdateList = new LinkedList<>();
        for (FileTemplate fileTemplate : effectList) {
            String filename = fileTemplate.getFilename();
            // 需要初始化文件模版的Key
            String string = filename.substring(0, filename.lastIndexOf("."));
            String substring = string;
            if (substring.contains(".")) {
                substring = string.substring(string.lastIndexOf(".") + 1);
            }
            String pinYinHeadChar = ChineseToPinyinUtil.getPinYinHeadChar(substring.replaceAll("（", "-").replaceAll("）", ""));
            FileTemplate e = new FileTemplate();
            e.setFileTemplateKey("MB_" + pinYinHeadChar);
            e.setFaceSignShowFlag(0);
            e.setId(fileTemplate.getId());
            needUpdateList.add(e);
        }
        fileTemplateService.updateBatchById(needUpdateList);
        Map<String, FileTemplate> fileTemplateMap = effectList.stream().collect(Collectors.toMap(FileTemplate::getFilename, Function.identity(), (k1, k2) -> k1));

        // 需要更新历史文件模版的Key
        List<FileTemplate> historyList = fileTemplateService.list(Wrappers.<FileTemplate>lambdaQuery()
                .eq(FileTemplate::getOutdated, 1));
        List<FileTemplate> needUpdateHistoryList = new LinkedList<>();
        for (FileTemplate fileTemplate : historyList) {
            FileTemplate template = fileTemplateMap.get(fileTemplate.getFilename());
            fileTemplate.setFileTemplateKey(template.getFileTemplateKey());
            fileTemplate.setFaceSignShowFlag(0);
            needUpdateHistoryList.add(fileTemplate);
        }
        fileTemplateService.updateBatchById(needUpdateHistoryList);

        // 上线的时候需要初始化历史文件模版的Key
        String[] arr =new String[]{"合同模版展示清单：",
                "船舶_融资租赁合同_直租",
                "1.融资租赁合同（直租）",
                "船舶_买卖合同_直租",
                "1-1.直租买卖合同（可根据实际情况修改）",
                "船舶_融资租赁合同_回租",
                "船舶_买卖合同_回租",
                "合同_租赁合同_主合同_回租_共同承租人",
                "合同_租赁合同_主合同_回租_单一承租人",
                "合同_保证合同_法人",
                "合同_保证合同_自然人",
                "合同_抵押合同_动产_一般抵押",
                "合同_抵押合同_附属_抵押物清单",
                "5.质押合同-股权",
                "6.质押合同-应收账款",
                "合同_咨询合同_共同承租人",
                "合同_咨询合同_单一承租人"};
        List<FileTemplate> needUpdateList1 = new LinkedList<>();
        for (FileTemplate template : effectList) {
            if (Arrays.stream(arr).anyMatch(s -> template.getFilename().contains(s))) {
                FileTemplate e = new FileTemplate();
                e.setId(template.getId());
                e.setFaceSignShowFlag(1);
                needUpdateList1.add(e);
            }
        }
        fileTemplateService.updateBatchById(needUpdateList1);
        log.info("FileTemplateKeyInitJob init end..........");
    }
}

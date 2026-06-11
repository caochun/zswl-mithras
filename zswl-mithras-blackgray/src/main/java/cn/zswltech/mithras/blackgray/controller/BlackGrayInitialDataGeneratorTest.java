package cn.zswltech.mithras.blackgray.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.blackgray.dto.external.VagueEnterpriseSearchREQ;
import cn.zswltech.mithras.blackgray.dto.external.VagueEnterpriseSearchRSP;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayLibraryMapper;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayWarehouseRecordMapper;
import cn.zswltech.mithras.blackgray.mapper.model.BlackGrayLibrary;
import cn.zswltech.mithras.blackgray.mapper.model.BlackGrayWarehouseRecord;
import cn.zswltech.mithras.blackgray.service.BlackGrayExternalDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 初始化黑灰名单
 *
 * @author ao.li
 * <br/>created on 2022/7/20 14:31
 */
@RestController
public class BlackGrayInitialDataGeneratorTest {


    @Resource
    private BlackGrayLibraryMapper blackGrayLibraryMapper;

    @Resource
    private BlackGrayWarehouseRecordMapper blackGrayWarehouseRecordMapper;

    @Resource
    private BlackGrayExternalDataService blackGrayExternalDataService;

    /**
     * 因为数据导入阶段，有的企业没有社会信用编码，故此接口查询外部数据补充
     */
    @GetMapping("black/gray/init")
    public void init() {
        List<BlackGrayLibrary> blackGrayLibrary = getBlackGrayLibrary();
        List<BlackGrayWarehouseRecord> blackGrayWarehouseRecord = getBlackGrayWarehouseRecord();
        List<String> nameList = new ArrayList<String>();
        if(ObjectUtil.isNotEmpty(blackGrayLibrary)){
            nameList.addAll(blackGrayLibrary.stream().map(BlackGrayLibrary::getEnterpriseName).collect(Collectors.toList()));
        }
        if(ObjectUtil.isNotEmpty(blackGrayWarehouseRecord)){
            nameList.addAll(blackGrayWarehouseRecord.stream().map(BlackGrayWarehouseRecord::getEnterpriseName).collect(Collectors.toList()));
        }
        Map<String, String> map = new HashMap<>();
        AtomicInteger i = new AtomicInteger();
        nameList.forEach(name -> {
            if(!map.containsKey(name)){
                VagueEnterpriseSearchREQ req = new VagueEnterpriseSearchREQ();
                req.setEnterpriseName(name);
                List<VagueEnterpriseSearchRSP> vagueEnterpriseSearchRSPS = blackGrayExternalDataService.vagueEnterpriseSearch(req);
                if(ObjectUtil.isNotEmpty(vagueEnterpriseSearchRSPS) ){
                    map.put(name, vagueEnterpriseSearchRSPS.get(0).getUnifiedSocialCreditCode());
                }
            }
            if(i.get() % 20 == 0){
                if(ObjectUtil.isNotEmpty(blackGrayLibrary)){
                    blackGrayLibrary.forEach(blackGrayLibrary1 -> {
                        String s = map.get(blackGrayLibrary1.getEnterpriseName());
                        if(ObjectUtil.isNotEmpty(s)){
                            blackGrayLibrary1.setUnifiedSocialCreditCode(s);
                            blackGrayLibraryMapper.updateByPrimaryKeySelective(blackGrayLibrary1);
                        }

                    });
                }
                if(ObjectUtil.isNotEmpty(blackGrayWarehouseRecord)){
                    blackGrayWarehouseRecord.forEach(blackGrayLibrary1 -> {
                        String s = map.get(blackGrayLibrary1.getEnterpriseName());
                        if(ObjectUtil.isNotEmpty(s)){
                            blackGrayLibrary1.setUnifiedSocialCreditCode(s);
                            blackGrayWarehouseRecordMapper.updateByPrimaryKeySelective(blackGrayLibrary1);
                        }

                    });
                }
                map.clear();
            }
            i.getAndIncrement();
        });

    }

    private List<BlackGrayLibrary> getBlackGrayLibrary(){
        Example example = new Example(BlackGrayLibrary.class);
        example.createCriteria().andIsNull(BlackGrayLibrary.UNIFIED_SOCIAL_CREDIT_CODE);
        return blackGrayLibraryMapper.selectByExample(example);
    }

    private List<BlackGrayWarehouseRecord> getBlackGrayWarehouseRecord(){
        Example example = new Example(BlackGrayWarehouseRecord.class);
        example.createCriteria().andIsNull(BlackGrayWarehouseRecord.UNIFIED_SOCIAL_CREDIT_CODE);
        return blackGrayWarehouseRecordMapper.selectByExample(example);
    }

}

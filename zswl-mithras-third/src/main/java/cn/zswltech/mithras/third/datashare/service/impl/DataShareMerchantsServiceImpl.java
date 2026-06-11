package cn.zswltech.mithras.third.datashare.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.zswltech.mithras.dto.client.share.DataShareREQ;
import cn.zswltech.mithras.third.datashare.mapper.DataShareMerchantsMapper;
import cn.zswltech.mithras.third.datashare.mapper.model.DataShareMerchants;
import cn.zswltech.mithras.third.datashare.service.DataShareMerchantsService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.*;

/**
 * @ClassName DataShareServiceImpl
 * @Description 客商信息维护，包括挡板
 * @Author jackerhe
 * @Date 2022/8/1 2:33 下午
 * @Version 1.0
 **/
@Service
public class DataShareMerchantsServiceImpl extends ServiceImpl<DataShareMerchantsMapper, DataShareMerchants> implements DataShareMerchantsService, InitializingBean {

    private static final String BATCH_GET_SINGLE_USER_CODE = "com.cncico.esb.opr.datashare.getBatchUserInfo.api";

    private List<DataShareMerchants> dataList = new ArrayList<>();

    /**
     * todo这是挡板使用，完成后需要删除
     * @author: jackerhe
     * @date: 2022/8/3 2:59 下午
     **/
    @Override
    public void afterPropertiesSet() throws Exception {
        getMerchantsExec();
    }

    @Override
    public DataShareMerchants getMerchants(DataShareREQ req) {
        return  this.baseMapper.selectOne(Wrappers.<DataShareMerchants>lambdaQuery()
                .eq(DataShareMerchants::getCreditCode, req.getCreditCode()));

    }

    public String getMerchantsMock(MultiValueMap<String, String> paramMap){
        Map map = new HashMap();
        Map data = new HashMap();
        if(BATCH_GET_SINGLE_USER_CODE.equals(paramMap.get("code").get(0))){
            String body = IoUtil.readUtf8(DataShareMerchantsService.class.getResourceAsStream("/mock/主数据.json"));
            JSONObject o = JSONObject.parseObject(body);
            return o.toJSONString();
        }
        int num = Integer.parseInt(paramMap.get("pageNum").get(0));

        int size = Integer.parseInt(paramMap.get("pageSize").get(0));
        List<DataShareMerchants> thisList = new ArrayList<>();
        for(int i = ((num-1)*size); i < Math.min(num*size, dataList.size()); i++){
            thisList.add(dataList.get(i));
        }

        map.put("code", "0000");
        map.put("message","success");
        data.put("total","10");
        data.put("list", JSONObject.toJSONString(thisList));
        map.put("data", JSONObject.toJSONString(data));
        return JSONObject.toJSONString(map);
    }

    private void getMerchantsExec(){
        String body = IoUtil.readUtf8(DataShareMerchantsService.class.getResourceAsStream("/mock/客商信息.json"));
        JSONObject o = JSONObject.parseObject(body);
        if(!"0000".equals(o.getString("code"))){
            return;
        }
        JSONObject data = o.getJSONObject("data");
        List<DataShareMerchants> list = JSONArray.parseArray(data.getString("list"), DataShareMerchants.class);
               for(DataShareMerchants share : list){
                   dataList.add(share);
               }
    }

}

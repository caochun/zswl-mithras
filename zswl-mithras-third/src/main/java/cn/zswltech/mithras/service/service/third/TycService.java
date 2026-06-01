package cn.zswltech.mithras.service.service.third;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.mapper.TycMockDataMapper;
import cn.zswltech.mithras.service.mapper.model.TycMockData;
import cn.zswltech.mithras.service.service.third.model.*;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.hutool.core.util.ObjectUtil.notEqual;
import static cn.zswltech.mithras.service.enums.TycErrorEnum.NO_DATA;
import static cn.zswltech.mithras.service.enums.TycErrorEnum.SUCCESS;
import static cn.zswltech.mithras.service.service.third.TycService.TycMockDataType.*;

/**
 * @author luyi
 * 天眼查查询服务
 */
@Slf4j
@Service
public class TycService {

    @Value("${mithras.tyc.mock}")
    private Boolean tycMock;
    @Value("${mithras.tyc.token}")
    private String tycToken;
    @Value("${mithras.http.proxy.host:}")
    private String proxyHost;
    @Value("${mithras.http.proxy.port:}")
    private String proxyPort;
    private Integer connTimeout = 3000;
    private final Map<String, List<String>> headers = new HashMap<>(1);
    private String defaultBody = "{\"reason\":\"ok\",\"error_code\":400001}";

    @Resource
    private TycMockDataMapper mockDataMapper;

    @PostConstruct
    public void init() {
        headers.put("Authorization", ListUtil.toList(tycToken));
    }

    private String get(String url) {
        HttpRequest request = HttpUtil.createGet(url)
                .header(headers)
                .setConnectionTimeout(connTimeout);
        if (isNotBlank(proxyHost) && isNotBlank(proxyPort)) {
            request.setHttpProxy(proxyHost, Integer.parseInt(proxyPort));
        }
        return request.execute().body();
    }

    public List<MithrasRelatedEnterpriseInfo> relatedEnterpriseInfo(String keyword) {
        try {
            String body = defaultBody;
            if (Boolean.TRUE.equals(tycMock)) {
                TycMockData mockData = mockDataMapper.selectOne(Wrappers.<TycMockData>lambdaQuery().eq(TycMockData::getDataType, RELATED_ENTERPRISE_INFO.name()).eq(TycMockData::getKeyword, keyword));
                if (isNotNull(mockData)) {
                    body = mockData.getJsonData();
                }
            } else {
                //https://open.tianyancha.com/open/823
                body = get("http://open.api.tianyancha.com/services/open/ic/inverst/2.0?pageSize=500&pageNum=1&keyword=" + keyword);
            }
            TycRsp<JSONObject> rsp = JSONUtil.toBean(body, new TypeReference<TycRsp<JSONObject>>() {
            }, true);

            if (notEqual(SUCCESS.getCode(), rsp.getErrorCode()) &&
                    notEqual(NO_DATA.getCode(), rsp.getErrorCode())) {
                log.error("天眼查获取对外投资信息错误.{}", body);
            }
            List<MithrasRelatedEnterpriseInfo> result = TycConvertor.relatedEnterpriseInfo(rsp.getResult());
            //过滤重复的
            List<MithrasRelatedEnterpriseInfo> filtered = new ArrayList<>();
            Set<String> nameSet = new HashSet<>();
            for (MithrasRelatedEnterpriseInfo info : result) {
                if (nameSet.contains(info.getEnterpriseName())) {
                    continue;
                }
                nameSet.add(info.getEnterpriseName());
                filtered.add(info);
            }
            return filtered;


        } catch (Exception e) {
            log.error("从天眼查获取对外投资失败,keyword:{}", keyword, e);
        }
        return new ArrayList<>();
    }

    public List<MithrasShareholderInfo> shareholderInfo(String keyword) {
        List<MithrasShareholderInfo> result = new ArrayList<>();
        try {
            String body = defaultBody;
            if (Boolean.TRUE.equals(tycMock)) {
                TycMockData mockData = mockDataMapper.selectOne(Wrappers.<TycMockData>lambdaQuery().eq(TycMockData::getDataType, SHAREHOLDER_INFO.name()).eq(TycMockData::getKeyword, keyword));
                if (isNotNull(mockData)) {
                    body = mockData.getJsonData();
                }
            } else {
                //https://open.tianyancha.com/open/821
                body = get("http://open.api.tianyancha.com/services/open/ic/holder/2.0?pageSize=500&pageNum=1&keyword=" + keyword);
            }
            TycRsp<JSONObject> rsp = JSONUtil.toBean(body, new TypeReference<TycRsp<JSONObject>>() {
            }, true);
            if (notEqual(SUCCESS.getCode(), rsp.getErrorCode()) &&
                    notEqual(NO_DATA.getCode(), rsp.getErrorCode())) {
                log.error("天眼查获取股东信息错误.{}", body);
            }
            result = TycConvertor.shareholderInfo(rsp.getResult());
        } catch (Exception e) {
            log.error("从天眼查获取基本股东失败,keyword:{}", keyword, e);
        }
        return result;
    }


    public MithrasBaseInfo baseInfo(String keyword) {
        try {
            String body = defaultBody;
            if (Boolean.TRUE.equals(tycMock)) {
                TycMockData mockData = mockDataMapper.selectOne(Wrappers.<TycMockData>lambdaQuery().eq(TycMockData::getDataType, BASE_INFO.name()).eq(TycMockData::getKeyword, keyword));
                if (isNotNull(mockData)) {
                    body = mockData.getJsonData();
                }
            } else {
                // https://open.tianyancha.com/open/819
                body = get("http://open.api.tianyancha.com/services/open/ic/baseinfoV3/2.0?keyword=" + keyword);
            }
            TycRsp<TycBaseInfo> rsp = JSONUtil.toBean(body, new TypeReference<TycRsp<TycBaseInfo>>() {
            }, true);
            if (notEqual(SUCCESS.getCode(), rsp.getErrorCode()) &&
                    notEqual(NO_DATA.getCode(), rsp.getErrorCode())) {
                log.error("天眼查获取基本信息错误.{}", body);
//                throw new MithrasException(ResultMsg.TYC_EXCEPTION + "（" + rsp.getReason() + "）");
            }
            return TycConvertor.mithrasBaseInfo(rsp.getResult());
        } catch (Exception e) {
            log.error("从天眼查获取基本信息失败,keyword:{}", keyword, e);
            return null;
        }
    }

    public List<MithrasCompanyInfo> queryByCompanyName(TycQueryCompanyReq req) {
        try {
            String body = defaultBody;
            if (Boolean.TRUE.equals(tycMock)) {
                TycMockData mockData = mockDataMapper.selectOne(Wrappers.<TycMockData>lambdaQuery().eq(TycMockData::getDataType, QUERY_COMPANY.name()).eq(TycMockData::getKeyword,req.getKeyword()));
                if (isNotNull(mockData)) {
                    body = mockData.getJsonData();
                }
            } else {
                log.info("调用天眼查模糊查询接口,request:{}",req);
                body = get("http://open.api.tianyancha.com/services/open/search/2.0?word=" + req.getCompanyName() + "&pageSize=" + req.getPageSize() + "&pageNum=" + req.getPage());
            }
            TycRsp<TycDataList> rsp = JSONUtil.toBean(body, new TypeReference<TycRsp<TycDataList>>() {
            }, true);
            if (notEqual(SUCCESS.getCode(), rsp.getErrorCode()) &&
                    notEqual(NO_DATA.getCode(), rsp.getErrorCode())) {
                log.error("天眼查模糊查询公司信息错误.{}", body);
//                throw new MithrasException(ResultMsg.TYC_EXCEPTION + "（" + rsp.getReason() + "）");
            }
            if (rsp.getResult() == null) {
                return null;
            }
            log.info("调用天眼查模糊查询接口,response:{total:{}}",rsp.getResult().getTotal());
            return TycConvertor.companyInfoConvertor(rsp.getResult());
        } catch (Exception e) {
            log.error("从天眼查模糊查询公司信息失败,req:{}", req, e);
            return null;
        }
    }

    public static enum TycMockDataType {
        BASE_INFO, SHAREHOLDER_INFO, RELATED_ENTERPRISE_INFO, QUERY_COMPANY
    }
}

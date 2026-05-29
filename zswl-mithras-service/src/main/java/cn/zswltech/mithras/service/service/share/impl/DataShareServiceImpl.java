package cn.zswltech.mithras.service.service.share.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.biz.service.OrgService;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.util.StringUtil;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.client.share.DataShareRegisterCustomREQ;
import cn.zswltech.mithras.dto.client.share.DataShareUserREQ;
import cn.zswltech.mithras.dto.client.share.DataShareUserRSP;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.datashare.DataShareCodeDict;
import cn.zswltech.mithras.service.mapper.model.datashare.DataShareManager;
import cn.zswltech.mithras.service.mapper.model.datashare.DataShareMerchants;
import cn.zswltech.mithras.service.mapper.model.datashare.DataShareOrg;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.share.DataShareManagerService;
import cn.zswltech.mithras.service.service.share.DataShareMerchantsService;
import cn.zswltech.mithras.service.service.share.DataShareService;
import cn.zswltech.mithras.service.util.PwdUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName DataShareServiceImpl
 * @Description 数据分享接口
 * @Author jackerhe
 * @Date 2022/8/3 3:26 下午
 * @Version 1.0
 **/
@Service
@Slf4j
public class DataShareServiceImpl implements DataShareService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DataShareManagerService dataShareManagerService;

    @Autowired
    private DataShareMerchantsService dataShareMerchantsService;

    @Autowired
    private DataShareCodeDictService dataShareCodeDictService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private UserService userService;

    @Resource
    private OrgService orgService;

    @Value("${data.share.loginUrl}")
    private String merchantLoginUrl;

    @Value("${data.share.merchantUrl}")
    private String merchantClientUrl;

    @Value("${data.share.batchUserUrl:}")
    private String batchUserUrl;

    @Value("${data.share.orgUrl:}")
    private String orgUrl;

    @Value("${data.share.register:}")
    private String registerUrl;

    @Value("${data.share.merchantSwitch}")
    private Boolean merchantSwitch;

    @Value("${data.share.clientId}")
    private String merchantClientId;

    @Value("${data.share.orgId}")
    private String merchantOrgId;

    @Value("${data.share.name}")
    private String merchantName;

    @Value("${data.share.pwd}")
    private String merchantPwd;

    @Value("${data.share.zlOrgId}")
    private Long zlOrgId;


    private static final String MERCHANT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDiOnj+yzhdNiH9B+RLWGY/1HhIP9xSv7XIJhgRnsbqa6oHEhUAAMX7/hyzQ3S9emIsFg5BsNMBBZeBrNzQ5LH8R8enB6K3oQ/u3F8gHlvDE+qZtAc9c2Ma60lUbJLKjcmLkD8V7pbxNIyiHLop3JoPXp3X76hf4R4dWwGZdIfzQIDAQAB";

    private static final String LOGIN_OPERATION_CODE = "com.cncico.esb.opr.datashare.cicossoserver.login";

    private static final String QUERYCLIENT_OPERATION_CODE = "com.cncico.esb.opr.datashare.clients.api";

    private static final String REGISTER_CODE = "com.cncico.esb.opr.crm.addMerchant.merchant";

    public static final String BATCH_GET_SINGLE_USER_CODE = "com.cncico.esb.opr.datashare.getBatchUserInfo.api";

    public static final String GET_ORGANIZATION_CODE = "com.cncico.esb.opr.datashare.organization.info";

    private static final String MERCHANT_MODEL = "merchant_model";

    private static final String MAIN_CODE = "main_code";

    private static final String PAGE_NUM = "pageNum";

    private static final String PAGE_SIZE = "pageSize";

    private static final String ORGID = "orgId";

    private static final String START_DATE = "startDate";

    private static final String START_TIME = "startTime";

    private static String pwdEncode;


    /**
     * 同步客商数据
     *
     * @author: jackerhe
     * @date: 2022/8/4 1:59 下午
     **/
    @Override
    public Boolean syncMerchants() {
        int tryNum = 2;
        int total;
        int startNum = 1;
        int startSize;
        Integer queryNum;
        // 生产1000， mock 3
        if (merchantSwitch) {
            startSize = 1000;
        } else {
            startSize = 3;
        }
        LocalDateTime nowDate = LocalDateTime.now();
        //获取管理信息信息
        DataShareManager dataShareManager = dataShareManagerService.getOne(Wrappers.<DataShareManager>lambdaQuery()
                .eq(DataShareManager::getModelName, MERCHANT_MODEL)
                .last(cn.zswltech.mithras.common.util.StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isEmpty(dataShareManager)) {
            //无记录，第一次查询，全量获取
            //重试三次
            total = saveMerchants(startNum, startSize, null, tryNum);
            if (total < 0) {
                return Boolean.FALSE;
            }
            //更新记录
            dataShareManager = new DataShareManager();
            dataShareManager.setDataTotal(total);
            dataShareManager.setPageNum(startNum);
            dataShareManager.setPageSize(startSize);
            dataShareManager.setStartTime(nowDate);
            dataShareManager.setModelName(MERCHANT_MODEL);
            dataShareManagerService.save(dataShareManager);
        } else {
            //非第一次查询
            queryNum = dataShareManager.getPageNum() + 1;

            //上次查询并未结束，继续查询
            total = saveMerchants(queryNum, dataShareManager.getPageSize(), dataShareManager.getEndTime() == null ? null : dataShareManager.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), tryNum);
            if (total <= 0) {
                return Boolean.FALSE;
            }
            double ceil = Math.ceil((double) total / Double.valueOf(dataShareManager.getPageSize()));
            Integer numTotal = NumberUtil.parseInt(String.valueOf(ceil));
            if (queryNum.compareTo(numTotal) >= 0) {
                //最后一次同步，更新管理信息
                dataShareManager.setEndTime(nowDate);
                dataShareManager.setDataTotal(total);
                dataShareManager.setPageNum(0);
                dataShareManager.setPageSize(startSize);
                dataShareManagerService.updateById(dataShareManager);
            } else {
                dataShareManagerService.update(Wrappers.<DataShareManager>lambdaUpdate()
                        .eq(DataShareManager::getModelName, MERCHANT_MODEL)
                        .set(DataShareManager::getPageNum, queryNum));
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean syscMainCode() {
        int tryNum = 2;
        int total;
        int startNum = 1;
        int startSize = 1000;
        Integer queryNum;
        LocalDateTime nowDate = LocalDateTime.now();
        //获取管理信息信息
        DataShareManager dataShareManager = dataShareManagerService.getOne(Wrappers.<DataShareManager>lambdaQuery()
                .eq(DataShareManager::getModelName, MAIN_CODE)
                .last(cn.zswltech.mithras.common.util.StringUtil.mysqlLimitOne()));

        if (ObjectUtil.isEmpty(dataShareManager)) {
            //无记录，第一次查询，全量获取
            //重试三次
            total = saveCode(startNum, startSize, null, tryNum);
            if (total < 0) {
                return Boolean.FALSE;
            }
            //更新记录
            dataShareManager = new DataShareManager();
            dataShareManager.setDataTotal(total);
            dataShareManager.setPageNum(startNum);
            dataShareManager.setPageSize(startSize);
            dataShareManager.setStartTime(nowDate);
            dataShareManager.setModelName(MAIN_CODE);
            dataShareManagerService.save(dataShareManager);
        } else {
            //非第一次查询
            queryNum = dataShareManager.getPageNum() + 1;
            //上次查询并未结束，继续查询
            total = saveCode(queryNum, dataShareManager.getPageSize(), dataShareManager.getEndTime() == null ? null : dataShareManager.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), tryNum);
            if (total <= 0) {
                return Boolean.TRUE;
            }
            double ceil = Math.ceil((double) total / Double.valueOf(dataShareManager.getPageSize()));
            Integer numTotal = NumberUtil.parseInt(String.valueOf(ceil));
            if (queryNum.compareTo(numTotal) >= 0) {
                //查询新的
                dataShareManager.setEndTime(nowDate);
                dataShareManager.setDataTotal(total);
                dataShareManager.setPageNum(0);
                dataShareManager.setPageSize(startSize);
                dataShareManagerService.updateById(dataShareManager);
            } else {
                dataShareManagerService.update(Wrappers.<DataShareManager>lambdaUpdate()
                        .eq(DataShareManager::getModelName, MAIN_CODE)
                        .set(DataShareManager::getPageNum, queryNum));
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public DataShareUserRSP getMainCode(DataShareUserREQ req) {
        //syscMainCode();
        //获取用户手机号
        String realPhone = userService.getRealPhone(req.getUserId());
        if (ObjectUtil.isNull(realPhone)) {
            throw new MithrasException("用户ID为空");
        }
        UserDO userDO = new UserDO();
        DataShareCodeDict codeDict = dataShareCodeDictService.getOne(Wrappers.<DataShareCodeDict>lambdaQuery()
                .eq(DataShareCodeDict::getPhone, realPhone)
                .orderByDesc(DataShareCodeDict::getUpdateTime)
                .last(cn.zswltech.mithras.common.util.StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNotNull(codeDict)) {
            userDO.setId(req.getUserId());
            userDO.setMainCode(codeDict.getUserId());
            userService.updateNotNull(userDO);
        }
        if (ObjectUtil.isNull(userDO.getMainCode())) {
            throw new MithrasException("暂无此用户数据编码");
        }
        return DataShareUserRSP.builder().mainCode(userDO.getMainCode()).build();
    }

    @Override
    public List<SelectRSP> getMainOrg() {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        List<DataShareOrg> dataList;
        String res;
        log.info("DataShareServiceImpl getMainOrg num : {}, size : {}", 1, 1000);
        paramMap.set(PAGE_NUM, "1");
        paramMap.set(PAGE_SIZE, String.valueOf(1000));
        paramMap.set(ORGID, merchantOrgId);
        try {
            res = getMerchantsExec(GET_ORGANIZATION_CODE, orgUrl, paramMap);
            if (ObjectUtil.isNull(res)) {
                return new ArrayList<>();
            }
            JSONObject jsonObject = JSONObject.parseObject(res);
            if (!ObjectUtil.isEmpty(jsonObject) && "0000".equals(jsonObject.getString("code"))) {
                JSONObject data = jsonObject.getJSONObject("data");
                dataList = JSONArray.parseArray(data.getString("list"), DataShareOrg.class);
                Set<Long> zlOrgs = new HashSet<>();
                zlOrgs.add(zlOrgId);
                Set<Long> children = new HashSet<>(zlOrgs);
                while (true) {
                    Set<Long> nextChildren = dataList.stream().filter(dataShareOrg -> children.contains(dataShareOrg.getParentId())).map(DataShareOrg::getOrgId).collect(Collectors.toSet());
                    if (CollectionUtil.isNotEmpty(nextChildren)) {
                        children.clear();
                        children.addAll(nextChildren);
                        zlOrgs.addAll(children);
                    } else {
                        break;
                    }
                }
                return dataList.stream().filter(dataShareOrg -> zlOrgs.contains(dataShareOrg.getParentId())).map(e -> new SelectRSP(e.getOrgName(), String.valueOf(e.getOrgId()))).collect(Collectors.toList());

            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            log.error("DataShareServiceImpl getMainOrg has error :", e);
        }
        return new ArrayList<>();
    }

    @Override
    public String registerCustom(DataShareRegisterCustomREQ req) {
        log.info("DataShareServiceImpl registerCustom req {}", req == null ? null : JSONUtil.toJsonStr(req));
        String rsp = postMerchantsExec(REGISTER_CODE, registerUrl, req, true);
        JSONObject jsonObject = JSONObject.parseObject(rsp);
        //0000 注册成功，100000已存在，无需注册
        if (!ObjectUtil.isEmpty(jsonObject) && StrUtil.equalsAny(jsonObject.getString("code"), "0000")) {
            log.info("registerCustom get code {}", rsp);
            return jsonObject.getString("data");
        } else if (!ObjectUtil.isEmpty(jsonObject) && StrUtil.equalsAny(jsonObject.getString("code"), "100000")) {
            String message = jsonObject.getString("message");
            if (ObjectUtil.isNotEmpty(message)) {
                String[] split = message.split("=");
                if (split.length == 2) {
                    return split[1];
                }
            }
            log.warn("registerCustom existed but not id {}", rsp);
            return null;
        } else {
            log.warn("registerCustom error {}", rsp);
            throw new MithrasException(rsp);
        }
    }

    private Integer saveMerchants(Integer num, Integer size, String date, int tryNum) {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        Map<String, String> codeMap;
        List<DataShareMerchants> dataList;
        String res;
        log.info("DataShareServiceImpl saveMerchants num : {}, size : {}, tryNum : {}", num, size, tryNum);
        paramMap.set(PAGE_NUM, String.valueOf(num));
        paramMap.set(PAGE_SIZE, String.valueOf(size));
        if (ObjectUtil.isNotNull(date)) {
            paramMap.set(START_DATE, date);
        }
        try {
            res = getMerchantsExec(QUERYCLIENT_OPERATION_CODE, merchantClientUrl, paramMap);
            if (ObjectUtil.isNull(res)) {
                return -1;
            }
            JSONObject jsonObject = JSONObject.parseObject(res);
            if (!ObjectUtil.isEmpty(jsonObject) && "0000".equals(jsonObject.getString("code"))) {
                JSONObject data = jsonObject.getJSONObject("data");
                //维护客商信息 优化可在数据库连接信息后加 rewriteBatchedStatements=true
                dataList = JSONArray.parseArray(data.getString("list"), DataShareMerchants.class);
                //每500条数据提交一次，防止占用过多内存
                if (ObjectUtil.isNotEmpty(dataList)) {
                    dataShareMerchantsService.saveOrUpdateBatch(dataList, 500);
                }
                //同步增量数据到client
                codeMap = new HashMap<>();
                dataList.forEach(rsp -> {
                    //区分自然人和法人
                    if (ObjectUtil.isNotEmpty(rsp.getCreditCode())) {
                        codeMap.put(rsp.getCreditCode(), String.valueOf(rsp.getClientId()));
                    }
                    if (ObjectUtil.isNotEmpty(rsp.getIdentificationNumber())) {
                        codeMap.put(rsp.getIdentificationNumber(), String.valueOf(rsp.getClientId()));
                    }
                });
                List<Client> clients = clientService.listByClientCodeIsNull();
                List<Client> clientsUpdate = new ArrayList<>();
                clients.forEach(rsp -> {
                    if (ClientType.CORPORATION.name().equals(rsp.getClientType()) && !StringUtil.isBlank(codeMap.get(rsp.getUscCode()))) {
                        rsp.setClientCode(codeMap.get(rsp.getUscCode()));
                        clientsUpdate.add(rsp);
                    } else if (!StringUtil.isBlank(codeMap.get(rsp.getCertNumber()))) {
                        rsp.setClientCode(codeMap.get(rsp.getCertNumber()));
                        clientsUpdate.add(rsp);
                    }
                });
                clientService.updateClientCode(clientsUpdate);
                return data.getInteger("total");
            } else {
                //可能是tocken过期了, 清空重新获取
                if (tryNum > 1) {
                    saveMerchants(num, size, date, --tryNum);
                }
            }
        } catch (Exception e) {
            log.error("DataShareServiceImpl saveMerchants has error data : {}, , tryNum : {}", date, tryNum, e);
            if (tryNum > 1) {
                saveMerchants(num, size, date, --tryNum);
            }
        }
        return -1;
    }

    private Integer saveCode(Integer num, Integer size, String date, int tryNum) {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        Map<String, String> codeMap = new HashMap<>();
        List<DataShareCodeDict> dataList;
        String res;
        log.info("DataShareServiceImpl saveCode num : {}, size : {}, tryNum : {}", num, size, tryNum);
        paramMap.set(PAGE_NUM, String.valueOf(num));
        paramMap.set(PAGE_SIZE, String.valueOf(size));
        paramMap.set(ORGID, merchantOrgId);
        if (!StringUtils.isBlank(date)) {
            paramMap.set(START_TIME, date);
        }
        try {
            res = getMerchantsExec(BATCH_GET_SINGLE_USER_CODE, batchUserUrl, paramMap);
            if (ObjectUtil.isNull(res)) {
                return -1;
            }
            JSONObject jsonObject = JSONObject.parseObject(res);
            if (!ObjectUtil.isEmpty(jsonObject) && "0000".equals(jsonObject.getString("code"))) {
                JSONObject data = jsonObject.getJSONObject("data");
                //维护客商信息 优化可在数据库连接信息后加 rewriteBatchedStatements=true
                dataList = JSONArray.parseArray(data.getString("list"), DataShareCodeDict.class);
                //每500条数据提交一次，防止占用过多内存
                if (ObjectUtil.isNotEmpty(dataList)) {
                    //集团会一直更新用户数据，这里先删除，后新增
                    List<String> collect = dataList.stream().map(DataShareCodeDict::getPhone).filter(ObjectUtil::isNotNull).collect(Collectors.toList());
                    List<Long> ids = dataShareCodeDictService.list(Wrappers.<DataShareCodeDict>lambdaQuery()
                            .in(DataShareCodeDict::getPhone, collect)).stream().map(DataShareCodeDict::getId).collect(Collectors.toList());
                    if (ObjectUtil.isNotEmpty(ids)) {
                        dataShareCodeDictService.removeByIds(ids);
                    }
                    dataShareCodeDictService.saveBatch(dataList, 500);
                }
                //fix 这里只保存，同步是页面按钮
                for (DataShareCodeDict dataShareCodeDict : dataList) {
                    codeMap.put(dataShareCodeDict.getPhone(), dataShareCodeDict.getUserId());
                }
                return data.getInteger("total");
            } else {
                //可能是tocken过期了, 清空重新获取
                if (tryNum > 1) {
                    saveCode(num, size, date, --tryNum);
                }
            }
        } catch (Exception e) {
            log.warn("DataShareServiceImpl saveCode has error data : {}, , tryNum : {}", date, tryNum, e);
            if (tryNum > 1) {
                saveCode(num, size, date, --tryNum);
            }
        }
        return -1;
    }


    public String merchantGetToken() {
        Map<String, String> param = new HashMap<>();
        if (StringUtil.isBlank(pwdEncode)) {
            try {
                DataShareServiceImpl.setPwdEncode(PwdUtils.encrypt(merchantPwd, MERCHANT_PUBLIC_KEY));
            } catch (Exception e) {
                log.warn("DataShareService merchantGetTocken encrypt pwd error {}", merchantPwd, e);
            }
        }
        param.put("username", merchantName);
        param.put("password", pwdEncode);
        String rsp = postMerchantsExec(LOGIN_OPERATION_CODE, merchantLoginUrl, param, false);
        JSONObject jsonObject = JSONObject.parseObject(rsp);
        if (!ObjectUtil.isEmpty(jsonObject) && "0000".equals(jsonObject.getString("code"))) {
            JSONObject data = jsonObject.getJSONObject("data");
            return data.getString("token");
        }
        return null;
    }


    private String postMerchantsExec(String operate, String url, Object o, boolean needToken) {
        if (!Boolean.TRUE.equals(merchantSwitch)) {
            //mock数据
            return IoUtil.readUtf8(DataShareMerchantsService.class.getResourceAsStream("/mock/客商信息.json"));
        }
        // header填充
        HttpHeaders httpHeaders = buildMerchantsHead(operate);
        if (needToken) {
            String token = merchantGetToken();
            if (ObjectUtil.isNull(token)) {
                return null;
            }
            httpHeaders.add("Authorization", token);
        }
        ResponseEntity<String> response = null;
        HttpEntity<String> request = new HttpEntity<>(JSONObject.toJSONString(o), httpHeaders);
        // 发送请求
        try {
            log.info("dataShare url:{}, request{}", url, request);
            response = restTemplate.postForEntity(url, request, String.class);
            log.info("dataShare url:{}, response{}", url, response);
        } catch (Exception e) {
            log.warn("DataShareServiceImpl postMerchants has error url : {}, o : {}", url, o, e);
        }
        return response == null ? null : response.getBody();
    }

    private String getMerchantsExec(String operate, String url, MultiValueMap<String, String> paramMap) {
        if (!Boolean.TRUE.equals(merchantSwitch)) {
            //mock数据
            paramMap.add("code", operate);
            return dataShareMerchantsService.getMerchantsMock(paramMap);
        }
        HttpHeaders httpHeaders = buildMerchantsHead(operate);
        String token = merchantGetToken();
        if (ObjectUtil.isNull(token)) {
            return null;
        }
        httpHeaders.add("Authorization", token);
        HttpEntity<MultiValueMap<String, Object>> formEntity = new HttpEntity<MultiValueMap<String, Object>>(httpHeaders);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.queryParams(paramMap).build().encode().toUri();
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, formEntity, String.class);
        return result.getBody();
    }


    private HttpHeaders buildMerchantsHead(String operate) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
        httpHeaders.add("Accept", MediaType.APPLICATION_JSON.toString());
        httpHeaders.add("OperationCode", operate);
        httpHeaders.add("ClientId", merchantClientId);
        return httpHeaders;
    }


    public static void setPwdEncode(String pwdEncode) {
        DataShareServiceImpl.pwdEncode = pwdEncode;
    }
}
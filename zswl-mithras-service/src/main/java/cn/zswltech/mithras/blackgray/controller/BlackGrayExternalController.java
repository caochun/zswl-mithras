package cn.zswltech.mithras.blackgray.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RestController;

/**
* @description 黑灰名单库 这里不能直接使用
* @author
* @date 2023-11-28
*/
@Api(tags = "黑灰名单库-外部接口")
@RestController
public class BlackGrayExternalController {

    /*private static final String RSA_PRIVATE_KEY = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMU+xO05gIfq9FKyp5JJXIGeifMgqJodvF9w/mrvkgSHZRtrtYiZtIzUfwfNTPSLXdP2XyL9yc369QbMbWnnmy5fnHufCroa6z+SeKvOZ5nHM8Wh3cW//Y6bsbRSWocCquJuUI35/88CCG9gnvcOFQaaD+rMdFe1lqO+PyfttXINAgMBAAECgYEAhZl8fUnjeyxzjMghpH44QDuIKLvIN4tImkTIrtnSR2sF2srbzMh/eJw95qTyKPl09ir8SZEo7XG8IXy552/OtSvazhuRV0EcGkjXjyjkrGfwsXQ4p+V4sOMNzsjAGpAXPwdEmxSP/h7I+dq1VqzZWLew1Sya02CQLnGJLGswbIECQQDnCFNGQNJNqxUBxQQuaHX5S33I+ItB79/HoJZN6ZtghNvnWWIrqEYpHEF0BtEjq+LkloIycYLTlWHaTiZ17WHRAkEA2o+xsW6BGWoJ5SfTlymW6VFQXZ42UPmE6jJk5yOoQuyChxU9E9E9Hw4nvmSswpVOYRHxRdLNtk389s1L3GZ/fQJBAONb9w+pFQ1oMukl6OJ+3LK0DxprNihylugO6jvMjKMkvIBlH05VcI5ehy9W0hRnwIcycviPaq8sUDsVM5ZUQTECQH+rzsXx4jIBUlRtRlmX7tLOMB7vy5TTnQPdejnXB3RIihr0miAYaxbYAvlh/9UOuKFVhNYfxREIT4uv8u+FZFUCQQDOewtVvHXDiIYg9hE8MfvENIZuZc0W0eH7FzByQYYIZ1weJN9WGeop9VEgQL4170YZjmBepIMTLFZy7F7vJuYY\n" +
            "公钥:MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDFPsTtOYCH6vRSsqeSSVyBnonzIKiaHbxfcP5q75IEh2Uba7WImbSM1H8HzUz0i13T9l8i/cnN+vUGzG1p55suX5x7nwq6Gus/knirzmeZxzPFod3Fv/2Om7G0UlqHAqriblCN+f/PAghvYJ73DhUGmg/qzHRXtZajvj8n7bVyDQIDAQAB";

    @Resource
    private BlackGrayLibraryService blackGrayLibraryService;
    @Resource
    private BlackGrayWarehouseRecordService blackGrayWarehouseRecordService;
    @Resource
    private BlackGrayBreakBusinessService blackGrayBreakBusinessService;

    @ApiOperation("黑灰名单库入库 -- 加密传输")
    @PostMapping("/public/black/gray/add")
    public R<Void> blackgrayAdd(@RequestBody BlackGraySubmitReq req) {
        // 1. 数据解密 RSA 非对称
        String jsonStr;
        try {
            jsonStr = RsaUtil.decrypt(req.getEncrypt(), RsaUtil.getPrivateKey(RSA_PRIVATE_KEY));
        } catch (Exception e) {
            throw new MithrasException("数据解密失败");
        }
        // 2. 反序列化
        List<BlackGrayWarehouseRecordAddREQ> addReqs = JSON.parseArray(jsonStr, BlackGrayWarehouseRecordAddREQ.class);
        // 3. 入库record表
        //todo 这里需要补充机构部门相关信息
        List<BlackGrayWarehouseRecord> records = BeanUtil.copyToList(addReqs, BlackGrayWarehouseRecord.class);
        blackGrayWarehouseRecordService.saveBatch(records);
        // 4. 尝试入库lib表
        List<BlackGrayLibrary> libs = BeanUtil.copyToList(records, BlackGrayLibrary.class);
        blackGrayLibraryService.attemptBatchWarehouse(libs);
        return R.success();
    }

    @ApiOperation("突破业务记录 -- 加密传输")
    @PostMapping("/public/break/business/add")
    public R<Void> breakBusinessAdd(@RequestBody BlackGraySubmitReq req) {
        // 1. 数据解密 RSA 非对称
        String jsonStr;
        try {
            jsonStr = RsaUtil.decrypt(req.getEncrypt(), RsaUtil.getPrivateKey(RSA_PRIVATE_KEY));
        } catch (Exception e) {
            throw new BusException("数据解密失败");
        }
        // 2. 反序列化入库
        List<BlackGrayBreakBusinessAddREQ> addReqs = JSON.parseArray(jsonStr, BlackGrayBreakBusinessAddREQ.class);
        Set<String> enterpriseNames = addReqs.stream().map(BlackGrayBreakBusinessAddREQ::getEnterpriseName).collect(Collectors.toSet());
        // 2.1 查询库中是否存在
        Map<String, Long> enterpriseIdMap = blackGrayLibraryService.listByNames(enterpriseNames).stream().collect(Collectors.toMap(BlackGrayLibrary::getEnterpriseName, BlackGrayLibrary::getId));
        addReqs.forEach(req1 -> {
            Long enterpriseId = enterpriseIdMap.get(req1.getEnterpriseName());
            if(ObjectUtil.isEmpty(enterpriseId)){
                throw new BusException("黑灰名单库中不存在企业:" + req1.getEnterpriseName() + ", 请先入库");
            }
            req1.setBlackGrayId(enterpriseId);
        });
        blackGrayBreakBusinessService.saveBatch(BeanUtil.copyToList(addReqs, BlackGrayBreakBusiness.class));
        return R.success();
    }

    @ApiOperation("对外接口-黑灰名单撞库查询")
    @PostMapping("/public/collision/library")
    public R<BlackGrayCollisionLibraryRSP> collisionLibrary(@RequestBody @Validated BlackGrayCollisionLibraryREQ req){
        BlackGrayLibrary one = blackGrayLibraryService.getOne(req);
        BlackGrayCollisionLibraryRSP rsp = new BlackGrayCollisionLibraryRSP();
        rsp.setEnterpriseName(req.getEnterpriseName());
        if(ObjectUtil.isEmpty(one)){
            rsp.setEnterpriseStatusCode(EnterpriseStatusEnum.NOT_HIT.name());
            rsp.setEnterpriseStatusName(EnterpriseStatusEnum.NOT_HIT.display);
            return R.success(rsp);
        }else {
            EnterpriseStatusEnum statusEnum = EnterpriseStatusEnum.valueOf(one.getBlackGrayType());
            rsp.setEnterpriseStatusCode(statusEnum.name());
            rsp.setEnterpriseStatusName(statusEnum.display);
            return R.success(rsp);
        }
    }

    *//**
     *
     * @param updateTime 更新时间
     * @param listType BLACK_LIST 黑名单 GRAY_LIST 灰名单
     * @param page 页
     * @param pageSize 页面大小
     * @return 摘要列表
     *//*
    @ApiOperation("数据摘要查询接口")
    @GetMapping("/public/black/gray/remark")
    public R<Map<Long, String>> remark(@RequestParam @NotNull String updateTime,
                                         @RequestParam @NotNull String listType,
                                         Integer page,
                                         Integer pageSize) {
        BlackGrayLibraryListREQ blackGrayLibraryListReq = new BlackGrayLibraryListREQ();
        if(ObjectUtil.isNotEmpty(page)){
            blackGrayLibraryListReq.setPage(page);
        }
        if(ObjectUtil.isNotEmpty(pageSize)){
            blackGrayLibraryListReq.setPageSize(pageSize);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime time = LocalDateTime.parse(updateTime,formatter);
        blackGrayLibraryListReq.setUpdateTime(time);
        blackGrayLibraryListReq.setListType(listType);
        BasePage<BlackGrayLibraryListRSP> pageList = blackGrayLibraryService.list(blackGrayLibraryListReq);
        // 摘要列表
        Map<Long, String> remarkMap = pageList.getList().stream().collect(Collectors.toMap(BlackGrayLibraryListRSP::getId, BlackGrayLibraryListRSP::getRemark));
        return R.success(remarkMap);
    }*/


}
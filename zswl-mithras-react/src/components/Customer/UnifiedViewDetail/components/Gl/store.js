import { makeAutoObservable, getQuery } from '@zswl/admin'
import { Modal, Table, PageStore, ModalStore, TableStore } from '@zswl/components'
import {
  getCredit, //授信
  getClassic,
  getCreditHistory,
  projectStatistics, // 项目阶段
  projList, //项目列表
  contractList, //合同列表,
  selectAll,
  getExternalList,
  getRatinghistory,
  getAuthFieldList,
} from '@/api/customerView/customerDetailApi'
import { message } from 'antd'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  credit = {}
  classic = {}
  creditHistory = {}
  detailId = ''
  projectStatisticsListCard = []
  uscc = ''
  enterpriseName = ''
  page = new PageStore({
    request: async ({ id }) => {
      const { uscc, enterpriseName } = getQuery()
      this.uscc = uscc
      this.enterpriseName = enterpriseName
      this.detailId = id
      this.init(id)
    },
  })

  detailData = {
    getRisk: {
      cRiskType: {
        riskMap: {
          风险事件: [],
          法律风险: [
            {
              middleaAcriptionOrg: '法律风险-浙商租赁',
              tagLevel: '重要风险',
              tagName: ['该企业股权存在被司法冻结'],
            },
          ],
          舆情风险: [],
          关联风险: [
            {
              middleaAcriptionOrg: '关联风险-浙商租赁',
              tagLevel: '次要风险',
              tagName: [
                '企业重要关联方出现被管控被处罚等负面舆情',
                '企业重要关联方出现无力偿还债务相关负面舆情',
                '企业重要关联方出现经营不利相关负面舆情',
              ],
            },
          ],
          信用风险: [],
          经营风险: [],
          道德风险: [],
          财务风险: [],
          担保方风险事件: [
            {
              middleaAcriptionOrg: '担保方风险事件-浙商租赁',
              tagLevel: '重要风险',
              tagName: [
                '该企业担保方出现无力偿还债务相关重大负面舆情',
                '该企业担保方出现经营不利相关重大负面舆情',
              ],
            },
            {
              middleaAcriptionOrg: '担保方风险事件-浙商租赁',
              tagLevel: '次要风险',
              tagName: [
                '该企业担保方出现经营不利相关较为重大负面舆情',
                '该企业担保方出现无力偿还债务相关较为重大负面舆情',
              ],
            },
          ],
        },
        riskList: [
          {
            tagCode: 'negative_ubopo_tag',
            triggerDate: '2024-09-12',
            modelId: '9',
            minorRisk: '担保方出现偿债能力类负面舆情',
            triggerDescribe:
              '企业：天邦食品股份有限公司\n事件名称：财务亏损或指标变差\n标题：ST天邦:预重整工作有序推进;二季度实现盈利,有望扛过猪周期?\n地址：https://www.chinabreed.com/home/toutiao/newsview/id/11553/cateids2/45.html\n\n企业：天邦食品股份有限公司\n事件名称：财务亏损或指标变差\n标题：涉案金额超11.9亿元!ST天邦收到执行通知书及裁定书\n地址：https://c.m.163.com/news/a/JBL478G505148I7Q.html\n\n企业：天邦食品股份有限公司\n事件名称：财务亏损或指标变差\n标题：ST天邦上半年盈利8.41亿元同比扭亏 正在推进预重整及重整申请\n地址：https://www.egsea.com/news/detail/1852622.html\n\n',
            tagName: '该企业担保方出现经营不利相关重大负面舆情',
            tagLevel: '重要风险',
            tagDescribe:
              '该企业担保方出现经营不利相关重大负面舆情，包括资产负债率过高/财务亏损/指标下滑/指标变差/资金回收风险/裁员/破产/流动性风险/流动性不足/出售资产等严重舆情',
            DrilData:
              '企业：天邦食品股份有限公司\n事件名称：财务亏损或指标变差\n标题：ST天邦:预重整工作有序推进;二季度实现盈利,有望扛过猪周期?\n地址：https://www.chinabreed.com/home/toutiao/newsview/id/11553/cateids2/45.html\n\n企业：天邦食品股份有限公司\n事件名称：财务亏损或指标变差\n标题：涉案金额超11.9亿元!ST天邦收到执行通知书及裁定书\n地址：https://c.m.163.com/news/a/JBL478G505148I7Q.html\n\n企业：天邦食品股份有限公司\n事件名称：财务亏损或指标变差\n标题：ST天邦上半年盈利8.41亿元同比扭亏 正在推进预重整及重整申请\n地址：https://www.egsea.com/news/detail/1852622.html\n\n',
            score: 50,
            modelName: '租赁特征_其他类_弱担保模型',
            ascriptionOrg: '浙商租赁',
            middleRisk: '担保方风险事件',
            createdTime: '2024-09-12',
          },
          {
            tagCode: 'negative_edpo_tag',
            triggerDate: '2024-09-12',
            modelId: '9',
            minorRisk: '担保方出现偿债能力类负面舆情',
            triggerDescribe:
              '企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：ST天邦:从公司4.8号开会预重整以来已经三月有余,看的出公司重整意愿不是很强,,下半年持续大幅盈利债务放松,公司是否有放弃重整的打算?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1778664062185328640\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：ST天邦:目前公司累计逾期债务约2.23亿元\n地址：https://news.qcc.com/postnews/92fefe05c7ab71b554d3dafd31ff88b7.html?pageSource=dynamic\n\n企业：天邦食品股份有限公司\n事件名称：拖欠款项\n标题：正在推进预重整及重整申请的ST天邦收到执行通知书及裁定书\n地址：https://news.yumi.com.cn/information/detail?id=336744\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：天邦食品:公司部分债务逾期\n地址：http://disc.static.szse.cn/download/disc/disk03/finalpage/2024-08-14/3871bc4e-7deb-4f5e-b875-77742b782c2e.PDF\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：[龙昌动保特约]ST天邦近2.23亿元债务逾期!法院刚同意预重整…\n地址：http://mp.weixin.qq.com/s?__biz=MjM5MDU5NDY3NQ==&mid=2651570607&idx=2&sn=8a12cb6fe7df54b7aeca5efd18fc0738&chksm=bc99fabff4e95ae1a41687d95d8e3c0a0eb76b6f0a0cb0685468d0caaafed4bf0feec4945917#rd\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：ST天邦:公司管理层应该考虑借住这波猪周期的上涨去清理债务,不应该采用重整的方式去清理债务!\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1778201270869250048\n\n企业：天邦食品股份有限公司\n事件名称：拖欠款项\n标题：ST天邦:收到执行通知书及执行裁定书,涉案金额1,193,456,584.46元\n地址：https://www.sohu.com/a/806045739_114984?scm=10001.325_13-109000.0.10140.5_32\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：ST天邦公告称,公司在银行、融资租赁公司等金融机构累计逾期的债务金额合计约2.23亿元\n地址：http://www.cnfin.com/kx/detail/20240814/4089302_1.html\n\n企业：天邦食品股份有限公司\n事件名称：拖欠款项\n标题：涉案金额超11.9亿元!ST天邦收到执行通知书及裁定书\n地址：https://c.m.163.com/news/a/JBL478G505148I7Q.html\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：现金流压力较大,无法清偿全部到期债务:ST天邦累计逾期债务金额达2.2亿元\n地址：https://baijiahao.baidu.com/s?id=1807356774372460317\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：ST天邦(002124.SZ)累计逾期债务金额合计约2.23亿元\n地址：http://www.zhitongcaijing.com/content/detail/1164057.html\n\n',
            tagName: '该企业担保方出现无力偿还债务相关重大负面舆情',
            tagLevel: '重要风险',
            tagDescribe:
              '该企业担保方出现无力偿还债务相关重大负面舆情，具体包括信用评级下调/评级关注/资金周转困难/违约/拖欠/债务重组等严重舆情',
            DrilData:
              '企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：ST天邦:从公司4.8号开会预重整以来已经三月有余,看的出公司重整意愿不是很强,,下半年持续大幅盈利债务放松,公司是否有放弃重整的打算?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1778664062185328640\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：ST天邦:目前公司累计逾期债务约2.23亿元\n地址：https://news.qcc.com/postnews/92fefe05c7ab71b554d3dafd31ff88b7.html?pageSource=dynamic\n\n企业：天邦食品股份有限公司\n事件名称：拖欠款项\n标题：正在推进预重整及重整申请的ST天邦收到执行通知书及裁定书\n地址：https://news.yumi.com.cn/information/detail?id=336744\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：天邦食品:公司部分债务逾期\n地址：http://disc.static.szse.cn/download/disc/disk03/finalpage/2024-08-14/3871bc4e-7deb-4f5e-b875-77742b782c2e.PDF\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：[龙昌动保特约]ST天邦近2.23亿元债务逾期!法院刚同意预重整…\n地址：http://mp.weixin.qq.com/s?__biz=MjM5MDU5NDY3NQ==&mid=2651570607&idx=2&sn=8a12cb6fe7df54b7aeca5efd18fc0738&chksm=bc99fabff4e95ae1a41687d95d8e3c0a0eb76b6f0a0cb0685468d0caaafed4bf0feec4945917#rd\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：ST天邦:公司管理层应该考虑借住这波猪周期的上涨去清理债务,不应该采用重整的方式去清理债务!\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1778201270869250048\n\n企业：天邦食品股份有限公司\n事件名称：拖欠款项\n标题：ST天邦:收到执行通知书及执行裁定书,涉案金额1,193,456,584.46元\n地址：https://www.sohu.com/a/806045739_114984?scm=10001.325_13-109000.0.10140.5_32\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：ST天邦公告称,公司在银行、融资租赁公司等金融机构累计逾期的债务金额合计约2.23亿元\n地址：http://www.cnfin.com/kx/detail/20240814/4089302_1.html\n\n企业：天邦食品股份有限公司\n事件名称：拖欠款项\n标题：涉案金额超11.9亿元!ST天邦收到执行通知书及裁定书\n地址：https://c.m.163.com/news/a/JBL478G505148I7Q.html\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：现金流压力较大,无法清偿全部到期债务:ST天邦累计逾期债务金额达2.2亿元\n地址：https://baijiahao.baidu.com/s?id=1807356774372460317\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：ST天邦(002124.SZ)累计逾期债务金额合计约2.23亿元\n地址：http://www.zhitongcaijing.com/content/detail/1164057.html\n\n',
            score: 50,
            modelName: '租赁特征_其他类_弱担保模型',
            ascriptionOrg: '浙商租赁',
            middleRisk: '担保方风险事件',
            createdTime: '2024-09-12',
          },
          {
            tagCode: 'judicial_freeze_tag',
            triggerDate: '2024-09-21',
            modelId: '1',
            minorRisk: '企业违法行为',
            triggerDescribe:
              '冻结标的企业：杭州萧山江南养殖有限公司\n被执行人：南京汉世伟食品有限公司\n冻结结标数额：30060万人民币元\n\n',
            tagName: '该企业股权存在被司法冻结',
            tagLevel: '重要风险',
            tagDescribe: '该企业股权被司法冻结',
            DrilData:
              '冻结标的企业：杭州萧山江南养殖有限公司\n被执行人：南京汉世伟食品有限公司\n冻结结标数额：30060万人民币元\n\n',
            score: 50,
            modelName: '风险画像_通用模型',
            ascriptionOrg: '浙商租赁',
            middleRisk: '法律风险',
            createdTime: '2024-09-21',
          },
          {
            tagCode: 'rp_negative_cppo_tag',
            triggerDate: '2024-09-11',
            modelId: '1',
            minorRisk: '关联方舆情风险',
            triggerDescribe:
              '企业：东营拾分味道食品有限公司\n事件名称：列入失信名单\n标题：东营拾分味道食品有限公司及其关联对象王友源被限制高消费[(2024)鲁0505执680号]\n地址：\n\n企业：蒙阴汉世伟食品有限公司\n事件名称：列入失信名单\n标题：蒙阴汉世伟食品有限公司及其关联对象邹君被限制高消费[(2024)鲁0982执2685号]\n地址：\n\n企业：郓城汉世伟食品有限公司\n事件名称：列入失信名单\n标题：郓城汉世伟食品有限公司及其关联对象刘湘被限制高消费[(2024)鲁1725执2894号]\n地址：\n\n企业：郓城汉世伟食品有限公司\n事件名称：列入失信名单\n标题：郓城汉世伟食品有限公司及其关联对象邹君被限制高消费[(2024)鲁1725执2894号]\n地址：\n\n企业：郓城汉世伟食品有限公司\n事件名称：列入失信名单\n标题：郓城汉世伟食品有限公司于2024年8月23日被列入失信被执行人名单\n地址：\n\n企业：河北汉世伟食品有限公司\n事件名称：列入失信名单\n标题：河北汉世伟食品有限公司及其关联对象张涛被限制高消费[(2024)冀1102执1930号]\n地址：\n\n企业：山东汉世伟食品有限公司\n事件名称：列入失信名单\n标题：山东汉世伟食品有限公司及其关联对象王志彪被限制高消费[(2024)鲁0982执2685号]\n地址：\n\n',
            tagName: '企业重要关联方出现被管控被处罚等负面舆情',
            tagLevel: '次要风险',
            tagDescribe:
              '任一一个该企业重要关联方出现“对外赔付”或“列入失信名单”或“列入观察名单”或”行政处罚“或“监管问询”或“刑事处罚”或"监管措施"或“撤销资质”等负面舆情',
            DrilData:
              '企业：东营拾分味道食品有限公司\n事件名称：列入失信名单\n标题：东营拾分味道食品有限公司及其关联对象王友源被限制高消费[(2024)鲁0505执680号]\n地址：null\n\n企业：蒙阴汉世伟食品有限公司\n事件名称：列入失信名单\n标题：蒙阴汉世伟食品有限公司及其关联对象邹君被限制高消费[(2024)鲁0982执2685号]\n地址：null\n\n企业：郓城汉世伟食品有限公司\n事件名称：列入失信名单\n标题：郓城汉世伟食品有限公司及其关联对象刘湘被限制高消费[(2024)鲁1725执2894号]\n地址：null\n\n企业：郓城汉世伟食品有限公司\n事件名称：列入失信名单\n标题：郓城汉世伟食品有限公司及其关联对象邹君被限制高消费[(2024)鲁1725执2894号]\n地址：null\n\n企业：郓城汉世伟食品有限公司\n事件名称：列入失信名单\n标题：郓城汉世伟食品有限公司于2024年8月23日被列入失信被执行人名单\n地址：null\n\n企业：河北汉世伟食品有限公司\n事件名称：列入失信名单\n标题：河北汉世伟食品有限公司及其关联对象张涛被限制高消费[(2024)冀1102执1930号]\n地址：null\n\n企业：山东汉世伟食品有限公司\n事件名称：列入失信名单\n标题：山东汉世伟食品有限公司及其关联对象王志彪被限制高消费[(2024)鲁0982执2685号]\n地址：null\n\n',
            score: 70,
            modelName: '风险画像_通用模型',
            ascriptionOrg: '浙商租赁',
            middleRisk: '关联风险',
            createdTime: '2024-09-11',
          },
          {
            tagCode: 'rp_negative_edpo_tag',
            triggerDate: '2024-09-11',
            modelId: '1',
            minorRisk: '关联方舆情风险',
            triggerDescribe:
              '企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：ST天邦:从公司4.8号开会预重整以来已经三月有余,看的出公司重整意愿不是很强,,下半年持续大幅盈利债务放松,公司是否有放弃重整的打算?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1778664062185328640\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：ST天邦:目前公司累计逾期债务约2.23亿元\n地址：https://news.qcc.com/postnews/92fefe05c7ab71b554d3dafd31ff88b7.html?pageSource=dynamic\n\n企业：淮安汉世伟食品有限公司\n事件名称：借款违约\n标题：淮安汉世伟食品有限公司2024年10月12日被上海票交所列入承兑人逾期名单\n地址：\n\n企业：天邦食品股份有限公司\n事件名称：拖欠款项\n标题：正在推进预重整及重整申请的ST天邦收到执行通知书及裁定书\n地址：https://news.yumi.com.cn/information/detail?id=336744\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：天邦食品:公司部分债务逾期\n地址：http://disc.static.szse.cn/download/disc/disk03/finalpage/2024-08-14/3871bc4e-7deb-4f5e-b875-77742b782c2e.PDF\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：[龙昌动保特约]ST天邦近2.23亿元债务逾期!法院刚同意预重整…\n地址：http://mp.weixin.qq.com/s?__biz=MjM5MDU5NDY3NQ==&mid=2651570607&idx=2&sn=8a12cb6fe7df54b7aeca5efd18fc0738&chksm=bc99fabff4e95ae1a41687d95d8e3c0a0eb76b6f0a0cb0685468d0caaafed4bf0feec4945917#rd\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：ST天邦:公司管理层应该考虑借住这波猪周期的上涨去清理债务,不应该采用重整的方式去清理债务!\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1778201270869250048\n\n企业：天邦食品股份有限公司\n事件名称：拖欠款项\n标题：ST天邦:收到执行通知书及执行裁定书,涉案金额1,193,456,584.46元\n地址：https://www.sohu.com/a/806045739_114984?scm=10001.325_13-109000.0.10140.5_32\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：ST天邦公告称,公司在银行、融资租赁公司等金融机构累计逾期的债务金额合计约2.23亿元\n地址：http://www.cnfin.com/kx/detail/20240814/4089302_1.html\n\n企业：建德农发牧业科技有限公司\n事件名称：拖欠款项\n标题：涉案金额超11.9亿元!ST天邦收到执行通知书及裁定书\n地址：https://c.m.163.com/news/a/JBL478G505148I7Q.html\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：现金流压力较大,无法清偿全部到期债务:ST天邦累计逾期债务金额达2.2亿元\n地址：https://baijiahao.baidu.com/s?id=1807356774372460317\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：ST天邦(002124.SZ)累计逾期债务金额合计约2.23亿元\n地址：http://www.zhitongcaijing.com/content/detail/1164057.html\n\n',
            tagName: '企业重要关联方出现无力偿还债务相关负面舆情',
            tagLevel: '次要风险',
            tagDescribe:
              '任一一个该企业重要关联方出现“信用评级下调”或“评级关注”“资金周转困难”或包含”违约“或包含“拖欠”或“债务重组”等负面舆情',
            DrilData:
              '企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：ST天邦:从公司4.8号开会预重整以来已经三月有余,看的出公司重整意愿不是很强,,下半年持续大幅盈利债务放松,公司是否有放弃重整的打算?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1778664062185328640\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：ST天邦:目前公司累计逾期债务约2.23亿元\n地址：https://news.qcc.com/postnews/92fefe05c7ab71b554d3dafd31ff88b7.html?pageSource=dynamic\n\n企业：淮安汉世伟食品有限公司\n事件名称：借款违约\n标题：淮安汉世伟食品有限公司2024年10月12日被上海票交所列入承兑人逾期名单\n地址：null\n\n企业：天邦食品股份有限公司\n事件名称：拖欠款项\n标题：正在推进预重整及重整申请的ST天邦收到执行通知书及裁定书\n地址：https://news.yumi.com.cn/information/detail?id=336744\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：天邦食品:公司部分债务逾期\n地址：http://disc.static.szse.cn/download/disc/disk03/finalpage/2024-08-14/3871bc4e-7deb-4f5e-b875-77742b782c2e.PDF\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：[龙昌动保特约]ST天邦近2.23亿元债务逾期!法院刚同意预重整…\n地址：http://mp.weixin.qq.com/s?__biz=MjM5MDU5NDY3NQ==&mid=2651570607&idx=2&sn=8a12cb6fe7df54b7aeca5efd18fc0738&chksm=bc99fabff4e95ae1a41687d95d8e3c0a0eb76b6f0a0cb0685468d0caaafed4bf0feec4945917#rd\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：ST天邦:公司管理层应该考虑借住这波猪周期的上涨去清理债务,不应该采用重整的方式去清理债务!\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1778201270869250048\n\n企业：天邦食品股份有限公司\n事件名称：拖欠款项\n标题：ST天邦:收到执行通知书及执行裁定书,涉案金额1,193,456,584.46元\n地址：https://www.sohu.com/a/806045739_114984?scm=10001.325_13-109000.0.10140.5_32\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：ST天邦公告称,公司在银行、融资租赁公司等金融机构累计逾期的债务金额合计约2.23亿元\n地址：http://www.cnfin.com/kx/detail/20240814/4089302_1.html\n\n企业：建德农发牧业科技有限公司\n事件名称：拖欠款项\n标题：涉案金额超11.9亿元!ST天邦收到执行通知书及裁定书\n地址：https://c.m.163.com/news/a/JBL478G505148I7Q.html\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：现金流压力较大,无法清偿全部到期债务:ST天邦累计逾期债务金额达2.2亿元\n地址：https://baijiahao.baidu.com/s?id=1807356774372460317\n\n企业：天邦食品股份有限公司\n事件名称：借款违约\n标题：ST天邦(002124.SZ)累计逾期债务金额合计约2.23亿元\n地址：http://www.zhitongcaijing.com/content/detail/1164057.html\n\n',
            score: 70,
            modelName: '风险画像_通用模型',
            ascriptionOrg: '浙商租赁',
            middleRisk: '关联风险',
            createdTime: '2024-09-11',
          },
          {
            tagCode: 'rp_negative_ubopo_tag',
            triggerDate: '2024-09-11',
            modelId: '1',
            minorRisk: '关联方舆情风险',
            triggerDescribe:
              '企业：天邦食品股份有限公司\n事件名称：财务亏损或指标变差\n标题：ST天邦:预重整工作有序推进;二季度实现盈利,有望扛过猪周期?\n地址：https://www.chinabreed.com/home/toutiao/newsview/id/11553/cateids2/45.html\n\n企业：天邦食品股份有限公司\n事件名称：财务亏损或指标变差\n标题：涉案金额超11.9亿元!ST天邦收到执行通知书及裁定书\n地址：https://c.m.163.com/news/a/JBL478G505148I7Q.html\n\n企业：天邦食品股份有限公司\n事件名称：财务亏损或指标变差\n标题：ST天邦上半年盈利8.41亿元同比扭亏 正在推进预重整及重整申请\n地址：https://www.egsea.com/news/detail/1852622.html\n\n',
            tagName: '企业重要关联方出现经营不利相关负面舆情',
            tagLevel: '次要风险',
            tagDescribe:
              '任一一个该企业重要关联方出现“资产负债率过高”“财务亏损”“指标下滑”“指标变差”“资金回收风险”“裁员”“破产”“流动性风险”“流动性不足”“出售资产“等负面舆情',
            DrilData:
              '企业：天邦食品股份有限公司\n事件名称：财务亏损或指标变差\n标题：ST天邦:预重整工作有序推进;二季度实现盈利,有望扛过猪周期?\n地址：https://www.chinabreed.com/home/toutiao/newsview/id/11553/cateids2/45.html\n\n企业：天邦食品股份有限公司\n事件名称：财务亏损或指标变差\n标题：涉案金额超11.9亿元!ST天邦收到执行通知书及裁定书\n地址：https://c.m.163.com/news/a/JBL478G505148I7Q.html\n\n企业：天邦食品股份有限公司\n事件名称：财务亏损或指标变差\n标题：ST天邦上半年盈利8.41亿元同比扭亏 正在推进预重整及重整申请\n地址：https://www.egsea.com/news/detail/1852622.html\n\n',
            score: 70,
            modelName: '风险画像_通用模型',
            ascriptionOrg: '浙商租赁',
            middleRisk: '关联风险',
            createdTime: '2024-09-11',
          },
          {
            tagCode: 'significant_ubopo_tag',
            triggerDate: '2024-09-11',
            modelId: '9',
            minorRisk: '担保方出现偿债能力类负面舆情',
            triggerDescribe:
              '企业：天邦食品股份有限公司\n事件名称：流动性风险\n标题：ST天邦上半年净现金流为-9.17亿元同比下滑368.44%,经营性现金流为负\n地址：https://www.toutiao.com/article/7408497262428521000/\n\n企业：天邦食品股份有限公司\n事件名称：流动性风险\n标题：ST天邦:从公司4.8号开会预重整以来已经三月有余,看的出公司重整意愿不是很强,,下半年持续大幅盈利债务放松,公司是否有放弃重整的打算?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1778664062185328640\n\n企业：天邦食品股份有限公司\n事件名称：财务亏损或指标变差\n标题：ST天邦:上半年扣非净利同增78.37% 二季度扭亏负债率下降\n地址：http://news.hexun.com/2024-08-29/214237772.html\n\n企业：天邦食品股份有限公司\n事件名称：流动性风险\n标题：ST天邦:公司管理层应该考虑借住这波猪周期的上涨去清理债务,不应该采用重整的方式去清理债务!\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1778201270869250048\n\n企业：天邦食品股份有限公司\n事件名称：流动性风险\n标题：ST天邦(002124)2024年中报简析:净利润增166.95%,短期债务压力上升\n地址：https://stock.stockstar.com/RB2024083100008787.shtml\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:尊敬的董秘,浙江法院网公告案号(2024)浙02民诉前调595号的申请破产重整的案件于8月9号开庭\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1790422703506313216\n\n企业：天邦食品股份有限公司\n事件名称：流动性风险\n标题：ST天邦:请问公司8月份销售量为什么环比下降了\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1809306987453116416\n\n企业：天邦食品股份有限公司\n事件名称：流动性风险\n标题：ST天邦2024年1-6月净利润为8.41亿元,较去年同期增长166.95%\n地址：https://www.toutiao.com/article/7408505794091582004/\n\n企业：天邦食品股份有限公司\n事件名称：财务亏损或指标变差\n标题：ST天邦上半年营收43.18亿元同比降8.84%,净利润8.41亿元同比增166.95%,销售费用同比增长32.61%\n地址：https://baijiahao.baidu.com/s?id=1808723375551782621\n\n',
            tagName: '该企业担保方出现经营不利相关较为重大负面舆情',
            tagLevel: '次要风险',
            tagDescribe:
              '该企业担保方出现经营不利相关较为重大负面舆情，具体包括资产负债率过高/财务亏损/指标下滑/指标变差/资金回收风险/裁员/破产/流动性风险/流动性不足/出售资产等严重舆情',
            DrilData:
              '企业：天邦食品股份有限公司\n事件名称：流动性风险\n标题：ST天邦上半年净现金流为-9.17亿元同比下滑368.44%,经营性现金流为负\n地址：https://www.toutiao.com/article/7408497262428521000/\n\n企业：天邦食品股份有限公司\n事件名称：流动性风险\n标题：ST天邦:从公司4.8号开会预重整以来已经三月有余,看的出公司重整意愿不是很强,,下半年持续大幅盈利债务放松,公司是否有放弃重整的打算?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1778664062185328640\n\n企业：天邦食品股份有限公司\n事件名称：财务亏损或指标变差\n标题：ST天邦:上半年扣非净利同增78.37% 二季度扭亏负债率下降\n地址：http://news.hexun.com/2024-08-29/214237772.html\n\n企业：天邦食品股份有限公司\n事件名称：流动性风险\n标题：ST天邦:公司管理层应该考虑借住这波猪周期的上涨去清理债务,不应该采用重整的方式去清理债务!\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1778201270869250048\n\n企业：天邦食品股份有限公司\n事件名称：流动性风险\n标题：ST天邦(002124)2024年中报简析:净利润增166.95%,短期债务压力上升\n地址：https://stock.stockstar.com/RB2024083100008787.shtml\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:尊敬的董秘,浙江法院网公告案号(2024)浙02民诉前调595号的申请破产重整的案件于8月9号开庭\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1790422703506313216\n\n企业：天邦食品股份有限公司\n事件名称：流动性风险\n标题：ST天邦:请问公司8月份销售量为什么环比下降了\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1809306987453116416\n\n企业：天邦食品股份有限公司\n事件名称：流动性风险\n标题：ST天邦2024年1-6月净利润为8.41亿元,较去年同期增长166.95%\n地址：https://www.toutiao.com/article/7408505794091582004/\n\n企业：天邦食品股份有限公司\n事件名称：财务亏损或指标变差\n标题：ST天邦上半年营收43.18亿元同比降8.84%,净利润8.41亿元同比增166.95%,销售费用同比增长32.61%\n地址：https://baijiahao.baidu.com/s?id=1808723375551782621\n\n',
            score: 70,
            modelName: '租赁特征_其他类_弱担保模型',
            ascriptionOrg: '浙商租赁',
            middleRisk: '担保方风险事件',
            createdTime: '2024-09-11',
          },
          {
            tagCode: 'significant_edpo_tag',
            triggerDate: '2024-09-11',
            modelId: '9',
            minorRisk: '担保方出现偿债能力类负面舆情',
            triggerDescribe:
              '企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:从公司4.8号开会预重整以来已经三月有余,看的出公司重整意愿不是很强,,下半年持续大幅盈利债务放松,公司是否有放弃重整的打算?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1778664062185328640\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:请公司快点进行预重整,预重整受理都如此困难,后期重整效率怎么办\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1777828759942062080\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦(SZ002124):公司与浙江建投合同纠纷案进入执行阶段,已进入预重整阶段,积极推进债务处置\n地址：https://finance.sina.cn/stock/relnews/dongmiqa/2024-09-20/detail-incpuwqn2868039.d.html?&cid=76524\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:为何预重整进展缓慢,公司与地方政府法院深交所沟通有间隙?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1785474153739931648\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：涉案金额超11.9亿元!ST天邦收到执行通知书及裁定书\n地址：https://c.m.163.com/news/a/JBL478G505148I7Q.html\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：现金流压力较大,无法清偿全部到期债务:ST天邦累计逾期债务金额达2.2亿元\n地址：https://baijiahao.baidu.com/s?id=1807356774372460317\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:尊敬的董秘,浙江法院网公告案号(2024)浙02民诉前调595号的申请破产重整的案件于8月9号开庭\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1790422703506313216\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:公司8月9日晚的公告显示,有意向投资人明确表示愿意参与预重整投资,希望公司向股东告知意向投资人具体是哪家公司或者机构\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1792597073649586176\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:如果宁波重整效率不高,建议公司更改注册地,在安徽进行预重整\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1774908448292425728\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:请问公司的财务重整法院预计何时会受理?现在处于什么阶段了?谢谢\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1776347064768090112\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:董秘您好,预重整受理公告中,公司提及得到了债权人、政府 、投资人的支持,公司后续重整进度应该会很快,请问公司重整下一步是啥?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1795655880638320640\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:请问董秘,浙江建投仲裁后,目的是否为了申请预重整成员,变为投资一方\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1809880726534938624\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：天邦公开招募重整投资人!年内已清退35个山东猪场\n地址：http://mp.weixin.qq.com/s?__biz=MzkyMjY0MDQ4NA==&mid=2247566151&idx=1&sn=ed643d48f0025bb79b76e7434ba674fc&chksm=c097522d2d6fad1496594a8d4a04f94bf0527bb7b12a6cb4be3194dda92f543988afca2f5bd7#rd\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:董秘您好,贵公司重整成功能降低负债率,有利于健康发展.目前公司和多少家在洽谈重整事宜?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1795655030234796032\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：[龙昌动保特约]ST天邦近2.23亿元债务逾期!法院刚同意预重整…\n地址：http://mp.weixin.qq.com/s?__biz=MjM5MDU5NDY3NQ==&mid=2651570607&idx=2&sn=8a12cb6fe7df54b7aeca5efd18fc0738&chksm=bc99fabff4e95ae1a41687d95d8e3c0a0eb76b6f0a0cb0685468d0caaafed4bf0feec4945917#rd\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:公司管理层应该考虑借住这波猪周期的上涨去清理债务,不应该采用重整的方式去清理债务!\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1778201270869250048\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:别的公司启动债权申报工作\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1828308300847415296\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:公司股票名称具体哪一天变成*st天邦?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1790738332062961664\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦(002124.SZ):6月末能繁母猪存栏约24.48万头\n地址：https://baijiahao.baidu.com/s?id=1805270212503826030\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:请问公司目前是否聘请专业律师团队对接重整?还是被动等待管理人分配处置公司破整?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1801710676424118272\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦(SZ002124):公司正积极推进重整准备工作,保持开放态度\n地址：https://finance.sina.cn/stock/relnews/dongmiqa/2024-07-22/detail-incezcxz1228636.d.html?&cid=76524\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:请问公司将于什么时候被浙江建投执行欠款?其中现金和法拍的金额预计是多少?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1809304067045908480\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:与浙江建投合同纠纷事项暂对生产影响较小\n地址：https://www.yicai.com/brief/102263586.html\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:目前公司预重整工作正在有序推进中\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1795654420405342208\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:法院决定对公司进行预重整\n地址：https://finance.ifeng.com/c/8bubPMOJ5Ga\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:公司目前重整进展不明,大大影响投资者的信心,希望公司通过正规窗口,向中小投资者宣传一下\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1783934854817243136\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:鉴于目前公司积极进行预重整,公司现金流问题是否考虑提前引入共益债权化解风险?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1781519536832335872\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：正在推进预重整及重整申请的ST天邦收到执行通知书及裁定书\n地址：https://news.yumi.com.cn/information/detail?id=336744\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:重整工作有序推进,预计年报后申请摘帽撤回重整\n地址：https://finance.sina.cn/stock/relnews/dongmiqa/2024-09-30/detail-incqxrws2829496.d.html?&cid=76524\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:近一个月能繁母猪大增42万头,希望公司加快重整,把握机遇,化解危机,重回龙头!\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1776566889960562688\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:公司申请重整以来,为何没有象其它公司那样:无论是否已经进入预重整或者重整,每个月至少公告一次重整进展情况?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1789843707555074048\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:预重整工作有序推进;二季度实现盈利,有望扛过猪周期?\n地址：https://www.chinabreed.com/home/toutiao/newsview/id/11553/cateids2/45.html\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:目前公司预重整准备工作正在有序推进中\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1774835296585768960\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：天邦食品(ST天邦SZ002124):公司预重整工作有序推进,债权人可申报债权,重整进展以公司公告为准\n地址：https://finance.sina.cn/stock/relnews/dongmiqa/2024-09-27/detail-incqqumt1053297.d.html?&cid=76524\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:表明公司的预重整申请还处于前期调查取证阶段，没有进入正式立案阶段。请问是否属实?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1790440261332619264\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:公开招募重整投资人\n地址：https://www.cls.cn/detail/1814628\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:请问公司是否在准备向证监会,最高法院报送材料以获得重整批复?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1810480449827794944\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：法院指定3家律师事务所担任ST天邦预重整管理人\n地址：https://www.sohu.com/a/804291849_114988?scm=10001.325_13-109000.0.10140.5_32\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:尊敬的董秘您好,8月9日,宁波中院的决定显示:天邦公司有能力且已经与主要债权人开展自主谈判\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1792703666255933440\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:请公司高管积极增持,稳定股价,为重整打气,为公司打气!另,目前公司是否有重整投资人对接?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1777969402941136896\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:目前公司是否就预重整参与宁波中院主持的听证会?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1778731269833576448\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:公司本次增持完毕,猪价持续冲高,贵公司还会不会继续回购增持增加市场投资者信心?贵公司重整战略投资人方便透露一下吗?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1783208541487751168\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:公司因为资金压力,在债权人的支持下最大限度保留并有效运营了部分产能\n地址：http://yuanchuang.10jqka.com.cn/20240904/c661442717.shtml\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:请问目前公司最大的债权人是谁?他对公司的财务重整是什么态度?谢谢\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1776348087146635264\n\n',
            tagName: '该企业担保方出现无力偿还债务相关较为重大负面舆情',
            tagLevel: '次要风险',
            tagDescribe:
              '该企业担保方出现无力偿还债务相关较为重大负面舆情，具体包括信用评级下调/评级关注/资金周转困难/违约/拖欠/债务重组等较严重舆情',
            DrilData:
              '企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:从公司4.8号开会预重整以来已经三月有余,看的出公司重整意愿不是很强,,下半年持续大幅盈利债务放松,公司是否有放弃重整的打算?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1778664062185328640\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:请公司快点进行预重整,预重整受理都如此困难,后期重整效率怎么办\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1777828759942062080\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦(SZ002124):公司与浙江建投合同纠纷案进入执行阶段,已进入预重整阶段,积极推进债务处置\n地址：https://finance.sina.cn/stock/relnews/dongmiqa/2024-09-20/detail-incpuwqn2868039.d.html?&cid=76524\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:为何预重整进展缓慢,公司与地方政府法院深交所沟通有间隙?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1785474153739931648\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：涉案金额超11.9亿元!ST天邦收到执行通知书及裁定书\n地址：https://c.m.163.com/news/a/JBL478G505148I7Q.html\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：现金流压力较大,无法清偿全部到期债务:ST天邦累计逾期债务金额达2.2亿元\n地址：https://baijiahao.baidu.com/s?id=1807356774372460317\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:尊敬的董秘,浙江法院网公告案号(2024)浙02民诉前调595号的申请破产重整的案件于8月9号开庭\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1790422703506313216\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:公司8月9日晚的公告显示,有意向投资人明确表示愿意参与预重整投资,希望公司向股东告知意向投资人具体是哪家公司或者机构\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1792597073649586176\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:如果宁波重整效率不高,建议公司更改注册地,在安徽进行预重整\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1774908448292425728\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:请问公司的财务重整法院预计何时会受理?现在处于什么阶段了?谢谢\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1776347064768090112\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:董秘您好,预重整受理公告中,公司提及得到了债权人、政府 、投资人的支持,公司后续重整进度应该会很快,请问公司重整下一步是啥?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1795655880638320640\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:请问董秘,浙江建投仲裁后,目的是否为了申请预重整成员,变为投资一方\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1809880726534938624\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：天邦公开招募重整投资人!年内已清退35个山东猪场\n地址：http://mp.weixin.qq.com/s?__biz=MzkyMjY0MDQ4NA==&mid=2247566151&idx=1&sn=ed643d48f0025bb79b76e7434ba674fc&chksm=c097522d2d6fad1496594a8d4a04f94bf0527bb7b12a6cb4be3194dda92f543988afca2f5bd7#rd\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:董秘您好,贵公司重整成功能降低负债率,有利于健康发展.目前公司和多少家在洽谈重整事宜?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1795655030234796032\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：[龙昌动保特约]ST天邦近2.23亿元债务逾期!法院刚同意预重整…\n地址：http://mp.weixin.qq.com/s?__biz=MjM5MDU5NDY3NQ==&mid=2651570607&idx=2&sn=8a12cb6fe7df54b7aeca5efd18fc0738&chksm=bc99fabff4e95ae1a41687d95d8e3c0a0eb76b6f0a0cb0685468d0caaafed4bf0feec4945917#rd\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:公司管理层应该考虑借住这波猪周期的上涨去清理债务,不应该采用重整的方式去清理债务!\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1778201270869250048\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:别的公司启动债权申报工作\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1828308300847415296\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:公司股票名称具体哪一天变成*st天邦?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1790738332062961664\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦(002124.SZ):6月末能繁母猪存栏约24.48万头\n地址：https://baijiahao.baidu.com/s?id=1805270212503826030\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:请问公司目前是否聘请专业律师团队对接重整?还是被动等待管理人分配处置公司破整?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1801710676424118272\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦(SZ002124):公司正积极推进重整准备工作,保持开放态度\n地址：https://finance.sina.cn/stock/relnews/dongmiqa/2024-07-22/detail-incezcxz1228636.d.html?&cid=76524\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:请问公司将于什么时候被浙江建投执行欠款?其中现金和法拍的金额预计是多少?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1809304067045908480\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:与浙江建投合同纠纷事项暂对生产影响较小\n地址：https://www.yicai.com/brief/102263586.html\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:目前公司预重整工作正在有序推进中\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1795654420405342208\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:法院决定对公司进行预重整\n地址：https://finance.ifeng.com/c/8bubPMOJ5Ga\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:公司目前重整进展不明,大大影响投资者的信心,希望公司通过正规窗口,向中小投资者宣传一下\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1783934854817243136\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:鉴于目前公司积极进行预重整,公司现金流问题是否考虑提前引入共益债权化解风险?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1781519536832335872\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：正在推进预重整及重整申请的ST天邦收到执行通知书及裁定书\n地址：https://news.yumi.com.cn/information/detail?id=336744\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:重整工作有序推进,预计年报后申请摘帽撤回重整\n地址：https://finance.sina.cn/stock/relnews/dongmiqa/2024-09-30/detail-incqxrws2829496.d.html?&cid=76524\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:近一个月能繁母猪大增42万头,希望公司加快重整,把握机遇,化解危机,重回龙头!\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1776566889960562688\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:公司申请重整以来,为何没有象其它公司那样:无论是否已经进入预重整或者重整,每个月至少公告一次重整进展情况?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1789843707555074048\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:预重整工作有序推进;二季度实现盈利,有望扛过猪周期?\n地址：https://www.chinabreed.com/home/toutiao/newsview/id/11553/cateids2/45.html\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:目前公司预重整准备工作正在有序推进中\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1774835296585768960\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：天邦食品(ST天邦SZ002124):公司预重整工作有序推进,债权人可申报债权,重整进展以公司公告为准\n地址：https://finance.sina.cn/stock/relnews/dongmiqa/2024-09-27/detail-incqqumt1053297.d.html?&cid=76524\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:表明公司的预重整申请还处于前期调查取证阶段，没有进入正式立案阶段。请问是否属实?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1790440261332619264\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:公开招募重整投资人\n地址：https://www.cls.cn/detail/1814628\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:请问公司是否在准备向证监会,最高法院报送材料以获得重整批复?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1810480449827794944\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：法院指定3家律师事务所担任ST天邦预重整管理人\n地址：https://www.sohu.com/a/804291849_114988?scm=10001.325_13-109000.0.10140.5_32\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:尊敬的董秘您好,8月9日,宁波中院的决定显示:天邦公司有能力且已经与主要债权人开展自主谈判\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1792703666255933440\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:请公司高管积极增持,稳定股价,为重整打气,为公司打气!另,目前公司是否有重整投资人对接?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1777969402941136896\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:目前公司是否就预重整参与宁波中院主持的听证会?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1778731269833576448\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:公司本次增持完毕,猪价持续冲高,贵公司还会不会继续回购增持增加市场投资者信心?贵公司重整战略投资人方便透露一下吗?\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1783208541487751168\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:公司因为资金压力,在债权人的支持下最大限度保留并有效运营了部分产能\n地址：http://yuanchuang.10jqka.com.cn/20240904/c661442717.shtml\n\n企业：天邦食品股份有限公司\n事件名称：债务重组\n标题：ST天邦:请问目前公司最大的债权人是谁?他对公司的财务重整是什么态度?谢谢\n地址：http://irm.cninfo.com.cn/ircs/question/questionDetail?questionId=1776348087146635264\n\n',
            score: 70,
            modelName: '租赁特征_其他类_弱担保模型',
            ascriptionOrg: '浙商租赁',
            middleRisk: '担保方风险事件',
            createdTime: '2024-09-11',
          },
        ],
        dataTime: '2025-01-01',
      },
    },
    getRiskScore: {
      crScore: {
        baseProfileScore: '60.97',
        comparePpScore: '0.00',
        changeList: [],
        changeListType: '近七天',
        changeDate: '2024-09-01',
        name: '信用风险',
        changeDescirbeList: [
          {
            type: '新增',
          },
          {
            describe: '企业存在除失信外被执行记录',
            type: '消除',
          },
          {
            describe: '该企业涉案涉诉',
            type: '消除',
          },
          {
            describe: '该企业担保方出现经营不利相关重大负面舆情',
            type: '新增',
          },
          {
            describe: '该企业担保方出现无力偿还债务相关重大负面舆情',
            type: '新增',
          },
          {
            describe: '该企业担保方出现无力偿还债务相关较为重大负面舆情',
            type: '新增',
          },
          {
            describe: '该企业担保方出现经营不利相关较为重大负面舆情',
            type: '新增',
          },
        ],
        newestScore: '50.00',
      },
      dataTime: '2025-01-01',
      scoreDetail: {
        commonScoreBranch: 8.38,
        commonScore: 83.8,
        featureScoreBranch: 35.36000000000001,
        masterRuleScorePercent: '50%',
        masterRuleScore: 64.8,
        masterRuleScoreBranch: 32.4,
        commonScorePercent: '10%',
        featureScore: 88.4,
        featureScorePercent: '40%',
      },
      baseScoreDetail: {
        commonScoreBranch: 8.940000000000001,
        commonScore: 89.4,
        featureScoreBranch: 30.400000000000002,
        masterRuleScorePercent: '50%',
        masterRuleScore: 43.25,
        masterRuleScoreBranch: 21.625,
        commonScorePercent: '10%',
        featureScore: 76,
        featureScorePercent: '40%',
      },
    },
  }
  async init(id) {
    this.projectStatisticsListCard = await projectStatistics(
      { clientId: id, permissionType: 'all' },
      'dashboardprojectstagestatisticsUnified'
    )
    this.credit = await getCredit({ clientId: id })
    this.classic = await getClassic({ clientId: id })
    this.creditHistory = await getCreditHistory({ clientId: id })
  }
  dashboardCardGroupEnum = []
  // 项目列表
  projectListStore = new Table.Store({
    request: async (params) => {
      const { dashboardCardGroupEnum } = await selectAll({})
      this.dashboardCardGroupEnum = dashboardCardGroupEnum
      return await projList({ clientId: this.detailId }) //项目列表
    },
  })
  // 合同列表
  contractListStore = new Table.Store({
    request: async (params) => {
      return await contractList({ clientId: this.detailId }) //合同列表
    },
  })

  score = ''
  effectTime = ''
  // 内部评级
  internalRating = new Table.Store({
    request: async (params) => {
      const data = await getRatinghistory({
        clientId: this.detailId,
      })

      if (!Array.isArray(data) || data.length === 0) {
        console.warn('不是一个数组再往下走就报错了 man !')
        return data
      }
      const currentDate = new Date().getTime()
      const closestData = data.reduce((closest, item) => {
        const itemTime = new Date(item?.createTime)?.getTime()
        const closestTime = new Date(closest?.createTime)?.getTime()
        return Math.abs(itemTime - currentDate) < Math.abs(closestTime - currentDate)
          ? item
          : closest
      }, data[0])
      if (closestData?.score) {
        this.score = closestData.score
        this.effectTime = closestData.effectTime
      }
      return data
    },
  })
  // 外部评级
  externalStore = new Table.Store({
    request: async (params) => {
      return await getExternalList({
        ...params,
        uscc: this.uscc,
        enterpriseName: this.enterpriseName,
      })
    },
  })
  InstrumentModuleStore = new Modal.Store({
    onFinish: async (closeVal) => {
      this.modal.close()
    },
  })
  ImitHighModuleStore = new Modal.Store({
    onFinish: async (closeVal) => {
      this.modal.close()
    },
  })
  authId = null
  unitModal = new Modal.Store({
    onOpen: async ({ id, ...p }) => {
      this.authId = id
      this.unitTable.setParams({ authId: id })
      this.unitTable.search({ authId: id })
    },
    onFinish: async (closeVal) => {
      this.unitModal.close()
    },
  })

  // 弹窗table
  unitTable = new Table.Store({
    request: async (params) => {
      return await getAuthFieldList(params)
    },
  })

  isSingle = null
  // 预警触发详情
  riskTriggerModal = new ModalStore({})

  riskListSingle = []
  riskTriggerTableData = {
    table: [],
    date: '',
  }
  riskTriggerTable = new TableStore({
    request: async (params) => {
      return this.riskTriggerTableData?.table
    },
  })
}

export default new Store()

import { PageStore, Modal, TableStore, ModalStore } from '@zswl/components'
import { history, makeAutoObservable } from '@zswl/admin'
import creditReportApi from '@/api/credit/creditReportApi'
import informationSummaryTableApi from '@/api/credit/informationSummaryTableApi'
import creditInformationApi from '@/api/credit/creditInformationApi'
import repaymentResponsibilityApi from '@/api/credit/repaymentResponsibilityApi'
import { message } from 'antd'
import payableLoansApi from '@/api/credit/payableLoansApi'
import creditLimitApi from '@/api/credit/creditLimitApi'

class Store {
  constructor({ checkBusinessRef, ...params }) {
    this.checkBusinessRef = checkBusinessRef
    this.params = params || {}
    makeAutoObservable(this)
  }
  params

  page = new PageStore({
    request: async (p) => {
      const id = this.params?.id
      return await creditReportApi.getBaseDetail({ id })
    },
  })
  checkBusinessRef = null
  /**
   * 打开征信报告Modal
   */
  openCreditReportModal = () => {
    this.creditReportModal.open()
  }

  /**
   * 征信报告ModalStore，打开时拉取信息概要详情与其他两块列表
   */
  creditReportModal = new ModalStore({
    onOpen: async () => {},
  })

  /**
   * 信息概要详情原始数据
   */
  infoSummaryDetail = null

  /**
   * 信息概要-顶部四项 表
   */
  infoSummaryHeaderTable = new TableStore({
    request: async () => {
      const d = this.infoSummaryDetail || {}
      return { list: [d] }
    },
  })

  /**
   * 信息概要-末尾统计项 表
   */
  infoSummaryStatTable = new TableStore({
    request: async () => {
      const d = this.infoSummaryDetail || {}
      const row = {
        nonCreditTransactionNumber: d.nonCreditTransactionNumber,
        taxArrearsRecordsNumber: d.taxArrearsRecordsNumber,
        civilJudgmentRecordsNumber: d.civilJudgmentRecordsNumber,
        mandatoryExecutionRecordsNumber: d.mandatoryExecutionRecordsNumber,
        administrativePenaltyRecordsNumber: d.administrativePenaltyRecordsNumber,
      }
      return { list: [row] }
    },
  })
  unsettledSummaryList = []
  /**
   * 未结清信贷及授信信息概要表 TableStore
   */
  unsettledSummaryTable = new TableStore({
    request: async (params) => {
      const { id: creditReportId, creditCode } = this.page.getData() ?? {}
      return creditInformationApi.postSummaryList({ ...params, creditReportId, creditCode })
    },
  })
  /**
   * 授信额度信息概要表 TableStore
   */
  creditSummaryTable = new TableStore({
    request: async (params) => {
      const { id: creditReportId, creditCode } = this.page.getData() ?? {}
      return creditLimitApi.postLimitList({ ...params, creditReportId, creditCode })
    },
  })
  /**
   * 相关还款责任信息概要表 TableStore
   */
  responsibilitySummaryTable = new TableStore({
    request: async (params) => {
      const { id: creditReportId, creditCode } = this.page.getData() ?? {}
      return repaymentResponsibilityApi.postResponsibilityList({
        ...params,
        creditReportId,
        creditCode,
      })
    },
  })
  /**
   * 应期借款概要表 TableStore
   */
  payableLoansTable = new TableStore({
    request: async (params) => {
      const { id: creditReportId, creditCode } = this.page.getData() ?? {}
      return payableLoansApi.postDetailsList({ ...params, creditReportId, creditCode })
    },
  })
  submit = async () => {
    const { id, clientInfos } = this.page.getData()
    const clientId = clientInfos?.map((v) => v?.clientId).filter(Boolean)
    const res = await creditReportApi.postBaseSubmit({ id, clientId })
    const tips = Array.isArray(res) ? res : []
    if (!tips.length) {
      message.success('提交申请成功')
      history.push('/creditManage/search?reload=true')
      return
    }
    Modal.info({
      title: '提示',
      content: (
        <div>
          <p>企业资料模块各分类均为必传，请将以下客户相关资料上传完整后再提交流程！</p>
          {tips.map((item, idx) => (
            <div key={idx} style={{ marginTop: 8 }}>
              <div style={{ color: 'red' }}>{item.clientName}：</div>
              {(item.creditReportFiles || []).map((f, i) => (
                <div key={i}>{f.materialsTypeName}</div>
              ))}
            </div>
          ))}
        </div>
      ),
    })
  }
}
export default Store

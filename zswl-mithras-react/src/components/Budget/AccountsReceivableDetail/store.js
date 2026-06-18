import { DrawerStore, FormStore, Modal, ModalStore, PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import accountsOverdueApi from '@/api/budget/accountsReceivable/accountsOverdueApi'
import integrationApi from '@/api/budget/accountsReceivable/integrationApi'
import { message } from 'antd'
import moment from 'moment'
import submitApi from '@/api/budget/accountsReceivable/submitApi'

/**
 * 应收账款详情页面的数据管理store
 */
class Store {
  constructor() {
    makeAutoObservable(this)
  }

  // 页面基础信息
  page = new PageStore({
    request: async (params) => {
      // 这里可以根据需要添加获取页面基础信息的接口
      return params
    },
  })

  // 应收逾期结算表
  overdueTable = new TableStore({
    request: async (params) => {
      const { id, processInstanceId } = this.page.getParams()
      return await accountsOverdueApi.postSettlementList({
        ...params,
        overdueReportId: id,
        processInstanceId,
      })
    },
  })

  // 应收逾期集成表
  detailTable = new TableStore({
    request: async (params) => {
      const { id, processInstanceId } = this.page.getParams()
      return await integrationApi.postIntegrationList({
        ...params,
        overdueReportId: id,
        processInstanceId,
      })
    },
  })

  form = new FormStore()

  // 新增/编辑弹窗
  addModal = new ModalStore({
    onOpen: async (record) => {
      if (record) {
        return {
          ...record,
          recordBillDate: record.recordBillDate && moment(record.recordBillDate),
          recordDueDate: record.recordDueDate && moment(record.recordDueDate),
        }
      }
      return {}
    },
    onFinish: async (values) => {
      await integrationApi.postIntegrationModify({
        ...values,
      })
      message.success('操作成功')
      this.addModal.close()
      this.detailTable.search()
    },
  })

  // 应收逾期集成单编辑弹窗
  overdueModal = new ModalStore({
    onOpen: async (record) => {
      if (record) {
        await this.onSelectClient(record.clientId, null, false)
        const findContract = this.contractList.find((item) => item.value === record.contractId)
        this.onSelectContract(record.contractId, findContract, false)
        return {
          ...record,
          collection: {
            value: record.collectionId,
            label: record.collectionCode,
          },
          contract: {
            value: record.contractId,
            label: record.contractCode,
          },
          recordBillDate: record.recordBillDate && moment(record.recordBillDate),
          settlementDate: record.settlementDate && moment(record.settlementDate),
          voucherAccountDate: record.voucherAccountDate && moment(record.voucherAccountDate),
        }
      }
      return {}
    },
    onFinish: async (values) => {
      const { id } = this.page.getParams()
      const newValues = {
        ...values,
        overdueReportId: id,
        contractId: values.contract?.value,
        contractCode: values.contract?.label,
        collectionId: values.collection?.value,
        collectionCode: values.collection?.label,
        settlementDate: values.settlementDate && moment(values.settlementDate).format('YYYY-MM-DD'),
      }
      if (!values?.id) {
        await accountsOverdueApi.postSettlementAdd(newValues)
      } else {
        await accountsOverdueApi.postSettlementModify({
          ...newValues,
        })
      }
      message.success('操作成功')
      this.overdueModal.close()
      this.overdueTable.search()
    },
  })
  contractList = []
  flowList = []
  onSelectClient = async (value, options, needRest = true) => {
    const form = this.overdueModal.getFormStore()
    const { id } = this.page.getParams()
    const res = await accountsOverdueApi.postContractRelation({
      clientId: value,
    })
    this.contractList =
      res?.map(({ contractId: value, contractCode: label, flowItem }) => ({
        label,
        value,
        children: flowItem?.map(({ flowId, flowCode }) => ({
          label: flowCode,
          value: flowId,
        })),
      })) || []
    if (needRest) {
      form.setFieldValue('contractId', undefined)
      form.setFieldValue('collectionId', undefined)
    }
  }
  onSelectContract = (value, options, needRest = true) => {
    const form = this.overdueModal.getFormStore()
    this.flowList = options?.children || []
    if (needRest) {
      form.setFieldValue('collectionId', null)
    }
  }

  isAddModal = true
  isOverdueAddModal = true

  /**
   * 编辑记录
   */
  editDetail = (record) => {
    console.log('record: ', record)
    this.addModal.open(record)
  }
  /**
   * 新增应收逾期集成单记录
   */
  addOverdue = () => {
    this.isOverdueAddModal = true
    this.overdueModal.open()
  }
  /**
   * 编辑应收逾期集成单记录
   */
  editOverdue = (record) => {
    this.overdueModal.open(record)
  }
  report = async () => {
    const { keys, rows } = this.detailTable.getSelected()
    const { keys: overdueKeys, rows: overdueRows } = this.overdueTable.getSelected()
    const { id: reportId } = this.page.getParams()

    const res = await integrationApi.postIntegrationPush({
      reportId,
      integrationRecordIds: keys,
      settlementRecordIds: overdueKeys,
    })
    Modal.info({
      title: '提示',
      content: (
        <div>
          <div>
            共推送{res?.count}条记录,成功{res.success}条,失败{res.failure}条
          </div>
          {!!res?.message?.length && (
            <div>
              <div> 失败记录:</div>
              {res?.message?.map((item) => (
                <div key={item}>{item}</div>
              ))}
            </div>
          )}
        </div>
      ),
    })
    this.detailTable.search()
    this.overdueTable.search()
  }
  submit = async () => {
    const { keys, rows } = this.detailTable.getSelected()
    const { keys: overdueKeys, rows: overdueRows } = this.overdueTable.getSelected()
    const { id: reportId } = this.page.getParams()
    await submitApi.postOverdueSubmit({
      integrationRecordIds: keys,
      settlementRecordIds: overdueKeys,
      reportId,
    })
    this.detailTable.search()
    this.overdueTable.search()
    message.success('提交成功')
  }
  $termDetailDrawer = new DrawerStore({})
  collectionId = null
  openTerm = (record) => {
    this.collectionId = record.collectionId
    this.$termDetailDrawer.open(record)
  }
}

export default new Store()

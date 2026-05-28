import { App, ModalStore, PageStore, TableStore, Modal } from '@zswl/components'
import { http, makeAutoObservable } from '@zswl/admin'
import { message } from 'antd'
import businessFundApi from '@/api/budget/flowCenter/businessFundApi'

class Store {
  constructor({ getCount }) {
    makeAutoObservable(this)
    this.getCount = getCount
  }
  flowType = 'PAY'
  radioChange = (e) => {
    this.flowType = e.target.value
    this.fundamentalsTable.reset()
  }

  fundamentalsTable = new TableStore({
    request: async (params) => {
      return businessFundApi.postFinanceList({ ...params, flowType: this.flowType })
    },
  })

  exportFile = async(rest) => {
    await http.post('/business/flow/finance/list/export', { ...rest, flowType: this.flowType },{
        type: 'download',
        timeout: 0,
    })
  }


  handleWriteOffFinish = async () => {
    const { rows } = this.fundamentalsTable.getSelected()
    Modal.confirm({
      title: '核销完毕',
      content: `是否将选中的 ${rows.length} 条流水状态更改为核销完毕？`,
      onOk: async () => {
        await businessFundApi.postManualPush({
          cashFlowCodeList: rows.map((item) => item.serialNo),
        })
        message.success('操作成功')
        this.fundamentalsTable.search()
      },
    })
  }

  offModal = new ModalStore({
    onOpen: async (record) => {
      const { paidInDate, ...rest } = record
      return {
        ...rest,
      }
    },
    onFinish: async (values) => {
      const { billCode, billAmount, billExpireDate, paymentMethod, serialNo, ...rest } = values
      const billManagementAddREQ = {
        billCode,
        billAmount,
        billExpireDate,
        mainId: values.paymentId,
        billType: 'PAYMENT',
      }
      await businessFundApi.postDetailSave({
        cashFlowCode: serialNo,
        billManagementAddREQ,
        ...rest,
      })
      this.offModal.close()
      message.success('核销成功')
      this.fundamentalsTable.search()
    },
  })

  detailTable = new TableStore({
    request: async (params) => {
      const { serialNo } = this.detailModal.getInitialValues()
      const res = await businessFundApi.postDetailList({ cashFlowCode: serialNo })
      return res
    },
  })
  detailModal = new ModalStore({
    onOpen: async (record) => {
      return record
    },
  })
  handleOff = () => {
    const isReceipt = this.flowType === 'PAY'
    const { rows } = this.fundamentalsTable.getSelected()
    const { cashFlowDate, ...rest } = rows[0]
    this.offModal.open({
      ...rest,
      cashFlowDate: cashFlowDate && moment(cashFlowDate),
    })
  }
  openDetail = async () => {
    const { rows } = this.fundamentalsTable.getSelected()
    const record = rows[0]
    this.detailModal.open(record)
  }
}
export default Store

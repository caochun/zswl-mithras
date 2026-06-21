import { TableStore, Modal, ModalStore, App } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from '@/api/cpm/payment/paymentApplicationApi'
import { amountStrToNumber, timeFormat } from '@/utils'
import { debounce as _debounce } from 'lodash'
import { message } from 'antd'

const { getData } = App
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  table = new TableStore({
    request: (searchData) => {
      const currentData = {
        ...searchData,
        applyPaymentAmountFrom:
          searchData.applyPaymentAmount && searchData.applyPaymentAmount[0]
            ? searchData.applyPaymentAmount[0] * 10000
            : undefined,
        applyPaymentAmountTo:
          searchData.applyPaymentAmount && searchData.applyPaymentAmount[1]
            ? searchData.applyPaymentAmount[1] * 10000
            : undefined,
        applyPaymentDateFrom: searchData.applyPaymentDate
          ? timeFormat(searchData.applyPaymentDate[0])
          : undefined,
        applyPaymentDateTo: searchData.applyPaymentDate
          ? timeFormat(searchData.applyPaymentDate[1])
          : undefined,
      }
      return Api.getList(currentData)
    },
  })
  editTable = new TableStore({
    request: (searchData) => {
      const currentData = {
        ...searchData,
        planedPaidAmountFrom:
          searchData.planedPaidAmount && searchData.planedPaidAmount[0]
            ? searchData.planedPaidAmount[0] * 10000
            : undefined,
        planedPaidAmountTo:
          searchData.planedPaidAmount && searchData.planedPaidAmount[1]
            ? searchData.planedPaidAmount[1] * 10000
            : undefined,
        planedPaidDateFrom: searchData.planedPaidDate
          ? timeFormat(searchData.planedPaidDate[0])
          : undefined,
        planedPaidDateTo: searchData.planedPaidDate
          ? timeFormat(searchData.planedPaidDate[1])
          : undefined,
      }
      return Api.getContractList(currentData)
    },
  })

  options = getData().optionsType
  getKeyOptionsLabelMap = (key) => {
    const obj = {}
    if (this.options && this.options[key]) {
      this.options[key].forEach((item) => {
        const { label, value } = item
        obj[value] = label
      })
    }
    return obj
  }
  /**
   * 撤回
   */
  remove = () => {
    Modal.confirm({
      title: '是否确定关闭付款申请？',
      onOk: async () => {
        const { keys } = this.table.getSelected()
        if (keys.length === 0) {
          return
        }
        const data = await Api.remove({ ids: keys })
        // const data =
        //   '编号:2022A0123-14-bzj失败原因:2022A0123-14-bzj单据不存在\\n编号:2022A0123-14-zf失败原因:2022A0123-14-zf单据不存在\\n编号:2022A0123-14-sf失败原因:2022A0123-14-sf单据不存在\\n'
        data &&
          message.info({
            content: (
              <div>
                {data?.split('\\n').map((v) => (
                  <div>{v}</div>
                ))}
              </div>
            ),
          })
        this.table.search()
      },
    })
  }

  // add = async (values) => {
  //   const res = await Api.postPayment(values)
  //   if (res) {
  //     message.info('创建成功！')
  //     history.push(`/cpm/paymentApplication/detail/${res.id}?isNew=true`)
  //     this.createModal.close()
  //   }
  // }
  createModal = new ModalStore({
    onFinish: async (values) => {
      const res = await Api.postPayment({
        ...values,
        clientId: values.clientId.value,
        contractCode: values.contractCode.label,
        contractId: values.contractCode.value,
        planedPaidAmount: amountStrToNumber(values.planedPaidAmount) * 10000,
        amountPaid: amountStrToNumber(values.amountPaid) * 10000,
        amountApplied: amountStrToNumber(values.amountApplied) * 10000,
        remainingApplyAmount: amountStrToNumber(values.remainingApplyAmount) * 10000,
      })
      message.info('创建成功！')
      history.push(`/cpm/paymentApplication/detail/${res.id}?isNew=true`)
      this.createModal.close()

      //   this.add({
      //     ...values,
      //     clientId: values.clientId.value,
      //     contractCode: values.contractCode.label,
      //     contractId: values.contractCode.value,
      //     planedPaidAmount: amountStrToNumber(values.planedPaidAmount) * 10000,
      //     amountPaid: amountStrToNumber(values.amountPaid) * 10000,
      //     amountApplied: amountStrToNumber(values.amountApplied) * 10000,
      //     remainingApplyAmount: amountStrToNumber(values.remainingApplyAmount) * 10000,
      //   })
    },
  })
}
export default new Store()

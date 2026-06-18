import { TableStore, ModalStore, PageStore, BlockStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import { message, Modal } from 'antd'
import { hasValue } from '@/utils'
import Api from '@/api/contract/prepayment'
import moment from 'moment'

class Store {
  constructor(data) {
    makeAutoObservable(this)
  }

  blockStore = new PageStore({
    request: async ({ contractId, businessVersion }) => {
      const { isEarlySettle, ...rest } = await Api.postPrepaymentList({
        contractId,
        businessVersion,
      })
      const newIsEarlySettle = hasValue(isEarlySettle) ? isEarlySettle : 0
      this.setIsEarlySettle(newIsEarlySettle)
      return {
        isEarlySettle: newIsEarlySettle,
        ...rest,
      }
    },
  })

  isEarlySettle = 0

  setIsEarlySettle = (value) => {
    this.isEarlySettle = value
  }

  onIsEarlySettleChange = async (value) => {
    // await this.blockStore.init()
    this.setIsEarlySettle(value)

    const { editRef } = this.blockStore.getParams()
    const form = editRef.current?.form
    const baseEdit = editRef.current?.baseEdit
    if (baseEdit) {
      this.calcLoss()
    }
  }

  calcTipArr = [
    `【提前终止补偿金】`,
    `【提前终止补偿金】、【未到期本金】、【未到期利息】、【保证金余额】`,
  ]

  onApplayRepaymentDateChange = async (value) => {
    if (value) {
      const { editRef, contractId } = this.blockStore.getParams()
      const { form } = editRef.current
      const { getFieldsValue, setFieldsValue, getFieldValue } = form
      const earlyRepayment =
        getFieldValue('earlyRepayment') ?? this.blockStore.getData()?.earlyRepayment

      const applayRepaymentDate = value && moment(value).format('YYYY-MM-DD')

      this.calcLoss({
        earlyRepayment: hasValue(earlyRepayment) ? earlyRepayment * 10000 : undefined,
        applayRepaymentDate: applayRepaymentDate,
      })
    }
  }

  calcLoss = async (params) => {
    const { editRef, contractId } = this.blockStore.getParams()
    const { form } = editRef.current
    const { getFieldsValue, setFieldsValue } = form
    const { applayRepaymentDate, earlyRepayment, isEarlySettle } = getFieldsValue(true)
    if (!hasValue(applayRepaymentDate)) {
      message.info('请选择提前还款日')
      return
    }
    const res = await Api.postPrepaymentCalculation({
      contractId,
      isEarlySettle,
      earlyRepayment: earlyRepayment * 10000,
      applayRepaymentDate: applayRepaymentDate && moment(applayRepaymentDate).format('YYYY-MM-DD'),
      ...params,
    })
    if (isEarlySettle) {
      setFieldsValue({
        loss: res.loss,
        beforeMaturityPrincipal: res.beforeMaturityPrincipal / 10000,
        beforeMaturityInterest: res.beforeMaturityInterest / 10000,
        earnestMoneyBalance: res.earnestMoneyBalance / 10000,
      })
      isEarlySettle && message.success(`${this.calcTipArr[isEarlySettle]}计算成功`)
    } else {
      setFieldsValue({
        loss: res.loss,
        penalty: res.penalty,
        earlyRepaymentInterest: res.earlyRepaymentInterest / 10000,
      })
      isEarlySettle && message.success(`${this.calcTipArr[isEarlySettle]}计算成功`)
    }
    return res
  }

  saveData = async (values) => {
    const { penaltyRow, lossRow, ...rest } = values
    const { contractId, editRef } = this.blockStore.getParams()
    const { form } = editRef.current
    const { getFieldsValue, setFieldsValue, getFieldValue } = form
    const earlyRepaymentInForm = getFieldValue('earlyRepayment')
    const earlyRepayment = earlyRepaymentInForm
      ? earlyRepaymentInForm * 10000
      : this.blockStore.getData()?.earlyRepayment

    const calcData = await this.calcLoss({
      earlyRepayment,
      applayRepaymentDate:
        values.applayRepaymentDate && moment(values.applayRepaymentDate).format('YYYY-MM-DD'),
    })

    let submitData = {
      ...this.blockStore.getData(),
      contractId,
      ...rest,
    }
    // 修改节点
    const { isStartUserModify } = this.blockStore.getParams()
    if (isStartUserModify) {
      submitData = {
        ...submitData,
        loss: calcData.loss,
        beforeMaturityPrincipal: calcData.beforeMaturityPrincipal,
        beforeMaturityInterest: calcData.beforeMaturityInterest,
        earnestMoneyBalance: calcData.earnestMoneyBalance,
      }
    }

    const { id } = this.blockStore.getData()
    if (id) {
      await Api.postPrepaymentModify({
        ...submitData,
        id,
        changeType: 'EARLY_REPAYMENT',
      })
    } else {
      await Api.postPrepaymentAdd({
        ...submitData,
      })
    }
    this.blockStore.init()
  }
}
export default Store

import { makeAutoObservable } from '@zswl/admin'
import { FormStore, ModalStore, PageStore, DrawerStore, TableStore } from '@zswl/components'
import Api from '@/api/cpm/collectionWriteOffApi'
import moment from 'moment'
import { message } from 'antd'
import mathjs from '@/utils/math'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  recordData
  penaltyInterestData
  page = new PageStore({
    request: async (params) => {
      const { id } = params
      const res = await Promise.all([
        Api.getCollectionWriteOffDetail({ id }),
        Api.postPenaltyInterestRecord({ id }),
      ])
      if (res) {
        return {
          ...res[0],
          penaltyInterestData: res[1],
        }
      }
    },
  })
  collectionTable = new TableStore({
    request: async () => {
      const res = await Api.postCollectionWriteOffList({ id: this.page.getParams().id })
      if (res) {
        this.recordData = res
        return res.records
      }
      return []
    },
  })

  collectionModal = new ModalStore({
    onFinish: async (values, initValues) => {
      const {
        file,
        collectionType,
        collectionDate,
        collectionAmount,
        principal,
        interest,
        penaltyInterest,
        postscript,
        ourAccountId,
        ourAccountNumber,
        ourAccountBank,
      } = values
      const formData = new FormData()
      const collectionAmountNumber = mathjs.toNonExponentialPlus(
        mathjs.format(mathjs.multiply(collectionAmount, 10000))
      )
      const principalNumber = mathjs.toNonExponentialPlus(
        mathjs.format(mathjs.multiply(principal, 10000))
      )
      const interestNumber = mathjs.toNonExponentialPlus(
        mathjs.format(mathjs.multiply(interest, 10000))
      )
      const penaltyInterestNumber = mathjs.toNonExponentialPlus(
        mathjs.format(mathjs.multiply(penaltyInterest, 10000))
      )
      // const sum = principalNumber + interestNumber + penaltyInterestNumber
      const sum = mathjs.toNonExponentialPlus(
        mathjs.format(
          mathjs
            .chain(principalNumber || 0)
            .add(interestNumber || 0)
            .add(penaltyInterestNumber || 0)
            .done()
        )
      )
      const noList = ['服务费/咨询费/手续费', '首期租金', '名义价款']
      if (
        Number(sum) !== Number(collectionAmountNumber) &&
        !noList.find((item) => item === this.page.getData().cashFlowItem)
      ) {
        message.info('请检查表单项 本金+利息+罚息不等于实收金额！')
        return
      }
      formData.append('collectionType', collectionType)
      formData.append('collectionDate', moment(collectionDate).format('yyyy-MM-DD'))
      formData.append('collectionAmount', collectionAmountNumber)
      formData.append('principal', principal ? principalNumber : '')
      formData.append('interest', interest ? interestNumber : '')
      formData.append('penaltyInterest', penaltyInterest ? penaltyInterestNumber : '')
      // formData.append('postscript', postscript || '')

      formData.append('ourAccountId', ourAccountId.value)
      formData.append('ourAccountName', ourAccountId.label)
      formData.append('ourAccountNumber', ourAccountNumber)
      formData.append('ourAccountBank', ourAccountBank)

      formData.append('collectionId', this.page.getParams().id)
      if (!initValues) {
        const { code, msg } = await Api.postCollectionWriteOffAdd(formData)
        if (code === 200) {
          message.info('新增成功')
          this.collectionModal.close()
          this.page.init()
          this.collectionTable.search()
        } else {
          msg && message.info(msg)
        }
      } else {
        formData.append('id', initValues.id)
        const { code, msg } = await Api.postCollectionWriteOffDetailModify(formData)
        if (code === 200) {
          message.info('修改成功')
          this.collectionModal.close()
          this.collectionTable.search()
          this.page.init()
        } else {
          msg && message.info(msg)
        }
      }
    },
  })
  writeOff = async () => {
    const { collectionAmount, principal, interest, penaltyInterest } = this.recordData
    const { code, msg } = await Api.postCollectionWriteoff({
      id: this.page.getParams().id,
      collectionAmount,
      principal,
      interest,
      penaltyInterest,
    })
    if (code === 200) {
      message.info('操作成功！')
      this.page.init()
    } else {
      msg && message.info(msg)
    }
  }
  unDoWriteoff = async () => {
    const { code, msg } = await Api.postCollectionWriteoffUnDo({
      id: this.page.getParams().id,
    })
    if (code === 200) {
      message.info('操作成功！')
      this.page.init()
    } else {
      msg && message.info(msg)
    }
  }
  getCollectionDetailData = async (id, noEdit, callback) => {
    const res = await Api.postCollectionWriteoffDetail({ id })
    if (res) {
      const { collectionAmount, interest, penaltyInterest, principal, ourBankInfo } = res
      const data = {
        ...res,
        collectionDate: res.collectionDate ? moment(res.collectionDate) : undefined,
        // collectionAmount: amountFormat(collectionAmount / 10000),
        collectionAmount: mathjs.toNonExponentialPlus(
          mathjs.format(mathjs.divide(collectionAmount, 10000))
        ),
        // interest: amountFormat(interest / 10000),
        interest: mathjs.toNonExponentialPlus(mathjs.format(mathjs.divide(interest, 10000))),
        // penaltyInterest: amountFormat(penaltyInterest / 10000),
        penaltyInterest: mathjs.toNonExponentialPlus(
          mathjs.format(mathjs.divide(penaltyInterest, 10000))
        ),
        // principal: amountFormat(principal / 10000),
        principal: mathjs.toNonExponentialPlus(mathjs.format(mathjs.divide(principal, 10000))),
        file: res.enclosureId
          ? [
              {
                name: res.enclosureName,
                id: res.enclosureId,
              },
            ]
          : [],
        ourAccountId: {
          label: ourBankInfo?.ourAccountName,
          value: ourBankInfo?.ourAccountId,
        },
        ourAccountBank: ourBankInfo?.ourAccountBank,
        ourAccountNumber: ourBankInfo?.ourAccountNumber,

        noEdit,
      }
      callback && callback(data)
      this.collectionModal.open(data)
    }
  }
  penaltyInterestModal = new ModalStore({
    onFinish: async (values) => {
      const { code, msg } = await Api.postPenaltyInterestModify({
        ...values,
        // penaltyInterestAmount: amountStrToNumber(values.penaltyInterestAmount) * 10000,
        penaltyInterestAmount: mathjs.toNonExponentialPlus(
          mathjs.format(mathjs.multiply(values.penaltyInterestAmount, 10000))
        ),
        id: this.page.getParams().id,
      })
      if (code === 200) {
        message.info('操作成功！')
        this.penaltyInterestModal.close()
        this.page.init()
      } else {
        msg && message.info(msg)
      }
    },
  })
  penaltyInterestModalTable = new TableStore({
    request: async (params) => {
      const res = await Api.postPenaltyInterestRecordHistory({
        ...params,
        id: this.page.getParams().id,
      })
      return res || []
    },
  })
  writeOffTable = new TableStore({
    request: async (params) => {
      const res = await Api.postCollectionWriteoffHistory({
        ...params,
        id: this.page.getParams().id,
      })
      if (res) {
        return res
      }
      return []
    },
  })
  onWriteoffChange = async (val, id) => {
    const { code, msg } = await Api.postCollectionWriteOffModify({
      id,
      writeOff: val,
    })
    if (code === 200) {
      message.info('修改成功！')
      this.writeOffTable.search()
      this.page.init()
    } else {
      msg && message.info(msg)
    }
  }
  billManageDraw = new DrawerStore({})
}
export default Store

import { makeAutoObservable } from '@zswl/admin'
import { ModalStore, PageStore, TableStore, DrawerStore, Modal, App } from '@zswl/components'
import { message } from 'antd'
import moment from 'moment'
import Api from '@/api/cpm/payment/paymentWriteOffDetailApi'
import paymentDetailApi from '@/api/cpm/payment/paymentDetailApi'
import { CloseOutlined } from '@ant-design/icons'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  actualDetail = {}

  page = new PageStore({
    request: async (params) => {
      const res = await Promise.all([
        Api.getPaymentWriteoffDetail(params),
        paymentDetailApi.getPaymentDetail({ id: params.paymentId }),
      ])
      const [writeOffDetail = {}, detail = {}] = res
      return { ...writeOffDetail, ...detail }
    },
  })
  confirmTable = new TableStore({
    request: async () => {
      const { paymentId } = this.page.getParams()
      const res = await Api.getPaymentWriteoffActualDetail({
        paymentId,
      })
      this.actualDetail = res
      return res.confirmedActualDetails ?? []
    },
  })
  collectionTable = new TableStore({
    request: async () => {
      const { paymentId } = this.page.getParams()
      const res = await Api.getPaymentWriteoffActualDetail({
        paymentId,
      })
      this.actualDetail = res
      return res.actualDetails ?? []
    },
  })

  unConfirmCollectionTable = new TableStore({
    request: async () => {
      const { paymentId } = this.page.getParams()
      const res = await Api.getPaymentWriteoffActualDetail({
        paymentId,
      })
      this.actualDetail = res
      // 需要过滤掉“已关闭”
      // return res.unconfirmedActualDetails?.filter((item) => item.writeOffStatus !== 'CLOSED') ?? []
      return res.unconfirmedActualDetails ?? []
    },
  })

  collectionModal = new ModalStore({
    onFinish: async (values, initValues) => {
      const data = {
        ...values,
        ourAccountNumber: undefined,
        ourAccountId: values.ourAccountNumber?.value,
        oppositeAccountNo: values.oppositeAccountNumber,
        paidInDate: moment(values.paidInDate).format('yyyy-MM-DD'),
        paymentId: this.actualDetail.paymentId,
      }
      if (!initValues.id) {
        await Api.postPaymentWriteoffActualDetailAdd(data)
        message.success('新增成功')
        this.collectionModal.close()
        this.unConfirmCollectionTable.search()
      } else {
        const paymentMethod = App.getData().optionsType.paymentMethod.filter(
          (item) => item.label === values.paymentMethod
        )
        await Api.postPaymentWriteoffActualDetailModify({
          ...data,
          id: initValues.id,
          paymentMethod: paymentMethod[0]?.value || values.paymentMethod,
        })
        message.success('修改成功')
        this.collectionModal.close()
        this.unConfirmCollectionTable.search()
      }
    },
  })

  deleteCollectItem = (id) => {
    Modal.confirm({
      title: `确认删除？`,
      onOk: async () => {
        await Api.postPaymentWriteoffActualDetailDetail({
          id,
        })
        message.success('删除成功！')
        this.unConfirmCollectionTable.search()
      },
    })
  }

  getCollectionDetailData = async ({ id, status, isConfirmed }) => {
    const res = await Api.postPayMentWriteoffDetail({ id, isConfirmed: isConfirmed ? 1 : 0 })
    const data = {
      ...res,
      paidInDate: res.paidInDate ? moment(res.paidInDate) : undefined,
      paidInAmount: res.paidInAmount,
      enclosure: res.enclosureId
        ? [
            {
              name: res.enclosureName,
              id: res.enclosureId,
            },
          ]
        : [],
      ourAccountNumber: {
        label: res.ourAccountNumber,
        value: res.ourAccountId,
      },
      ourAccountId: {
        label: res.accountName,
        value: res.ourAccountId,
      },
      oppositeAccountId: {
        label: res.oppositeAccountName,
        value: res.oppositeAccountId,
      },
      _pageStatus: status,
    }
    this.collectionModal.open(data)
  }

  billManageDraw = new DrawerStore({})
  confirmModal = new ModalStore({})
  confirmCancel = async () => {
    const paymentId = this.actualDetail.paymentId
    await Api.postPaymentWriteoffActualDetailSubmit({ isFinishPut: false, paymentId })
    this.page.init()
    this.confirmModal.close()
    message.success('提交成功')
  }
  confirmOk = async () => {
    Modal.confirm({
      title: `系统提示`,
      content: '本次投放之后，该笔付款申请单将完成全额投放，请再次确认！',
      closable: true,
      onOk: async () => {
        this.confirmModal.close()
        this.collectionDateModal.open()
      },
    })
  }
  submit = async () => {
    if (this.actualDetail.amountIsSame) {
      Modal.confirm({
        title: `系统提示`,
        content: '本次投放之后，该笔付款申请单将完成全额投放，请再次确认！',
        closable: true,
        onOk: async () => {
          this.collectionDateModal.open()
        },
      })
    } else {
      this.confirmModal.open()
    }
  }
  advanceSubmit = async () => {
    const paymentId = this.actualDetail.paymentId
    await Api.postPaymentReviewinadvancedSubmit({ id: paymentId })
    message.success('提前提交审批成功')
  }
  collectionDateModal = new ModalStore({
    onFinish: async (data) => {
      // 获取当前日期
      const currentDate = new Date();
      const day = currentDate.getDate();
      data.sameStartDate = data.sameStartDate.value
      if (data.sameStartDate&&day >= 25) {
        Modal.confirm({
          title: `系统提示`,
          content:  <div style={{color:'red'}}>
          <p>1.是否匹配融资；</p>
          <p>2.若未匹配融资，除已与财务沟通确认外，收租日同起租日（晚于25日的收租日为25日）。</p>
        </div>,
          closable: true,
          onOk: async () => {
            const paymentId = this.actualDetail.paymentId
            await Api.updateCollectionDay({ ...data, paymentId })
            await Api.postPaymentWriteoffActualDetailSubmit({ isFinishPut: true, paymentId })
            this.page.init()
            message.success('提交成功')
            this.collectionDateModal.close()
          },
        })
        return
      }
      const paymentId = this.actualDetail.paymentId
      await Api.updateCollectionDay({ ...data, paymentId })
      await Api.postPaymentWriteoffActualDetailSubmit({ isFinishPut: true, paymentId })
      this.page.init()
      message.success('提交成功')
      this.collectionDateModal.close()
    },
  })

  pushBank = async () => {
    const { paymentId, businessVersion } = this.page.getParams()
    Modal.confirm({
      title: `确认推送？`,
      closable: true,
      onOk: async () => {
        const res2 = await Api.postSendAdvanceApplication({ id: paymentId, businessVersion })
        res2.msg && message.info(res2.msg)
      },
    })
  }
}
export default Store

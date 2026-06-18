import { ContractOperationMap as contractOperationMap } from '@/components/Contract/DetailEntries'
import { timeFormat, userIsProjSponsor } from '@/utils'
import { history, makeAutoObservable } from '@zswl/admin'
import { Modal, ModalStore, PageStore, TableStore } from '@zswl/components'
import { message } from 'antd'
import Api from '@/api/contract/baseInfo'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  selectedKey = () => {
    const { rows } = this.$table.getSelected()
    if (rows.length > 0) {
      const { id, bizType, contractStatus, projSponsorUserId, contractProcessStatus, contractProcessName, clientName, clientId } = rows[0]
      return {
        id,
        bizType,
        contractStatus,
        contractProcessStatus,
        projSponsorUserId,
        contractProcessName,
        clientName,
        clientId,
      }
    }
    return null
  }

  page = new PageStore({})

  $table = new TableStore({
    request: (searchData) => {
      const currentData = {
        ...searchData,
        createFrom: searchData.createDate ? timeFormat(searchData.createDate[0]) : undefined,
        createTo: searchData.createDate ? timeFormat(searchData.createDate[1]) : undefined,
        updateFrom: searchData.updateDate ? timeFormat(searchData.updateDate[0]) : undefined,
        updateTo: searchData.updateDate ? timeFormat(searchData.updateDate[1]) : undefined,
        createDate: undefined,
        updateDate: undefined,
      }
      return Api.postContractList(currentData)
    },
  })

  // 作废合同
  remove = (record) => {
    Modal.confirm({
      title: `请确认是否作废合同：${record.contractCode}？`,
      onOk: async () => {
        const { code, msg } = await Api.removeContract({ id: record.id })
        if (code === 200) {
          message.success('操作成功')
          this.$table.search()
        } else {
          message.info(msg)
        }
      },
    })
  }

  // 创建合同
  createModal = new ModalStore({
    onFinish: async (values) => {
      const {
        projName: { label },
        leaseType,
        bizType,
        ...rest
      } = values
      try {
        const data = await Api.postCreateContract({
          bizType,
          projName: label,
          leaseType: leaseType ? leaseType.key : '',
          ...rest,
        })
        this.createModal.close()
        this.$table.search()
        history.push(`/contract/list/detail/${data.id}?bizType=${bizType}&isCreate=1`)
      } catch (error) {
        console.log({ error })
      }
    },
  })

  // 流程判断
  availAbleType
  changeCheck = async (flows, type, callBack) => {
    const { id, projSponsorUserId } = this.selectedKey()
    if (!userIsProjSponsor(projSponsorUserId)) {
      message.info('非项目发起人，无权编辑该合同！')
      return
    }
    const { data, code, msg } = await Api.changeCheck({
      contractId: id,
      moduleType: flows,
    })
    /* eslint-disable */
    if (code === 200) {
      if (!data.canProcess) {
        message.info(data.message)
      } else {
        if (data.twoStatus) {
          this.availAbleType = data.twoStatus
          callBack()
        } else {
          if (type === 'change') {
            this.$changeModal.open()
          } else if (type === 'settle') {
            this.onContractSettlement()
          } else if (type === 'rent' || type === 'receipt' || type === 'prepayment') {
            callBack()
          }
        }
      }
    } else {
      message.info(msg)
    }
  }

  // 准备进行合同操作
  contractOptPre = async (operation, callBack) => {
    const { id } = this.selectedKey()
    try {
      const res = await Api.contractOptPre({
        contractId: id,
        operation,
      })
      callBack?.()
    } catch (error) {
      if ('NO_WRITE_OFF_COMPLETED' == error.split('@')[0]) {
        Modal.confirm({
          title: '提示',
          content: error.split('@')[1],
        })
        return
      }
    }
  }

  checkSelected = () => {
    if (!this.selectedKey()) {
      message.info('选中项为空')
      return
    }
  }

  // 合同变更
  $changeModal = new ModalStore({
    onFinish: async (values) => {
      const { id, bizType } = this.selectedKey()
      const { changeType } = values
      if (changeType === 'OTHER') {
        this.contractOptPre(contractOperationMap[values.changeType], () => {
          history.push(`/contract/list/detail/${id}?formChangeOther=true&bizType=${bizType}`)
        })
      } else {
        this.contractOptPre(contractOperationMap[values.changeType], () => {
          history.push(`/contract/list/change/${id}?changeType=${values.changeType}&bizType=${bizType}`)
        })
      }
      this.$changeModal.close()
    },
  })
  // 合同变更
  onContractChange = async () => {
    this.checkSelected()
    const { id, bizType, contractProcessStatus, contractProcessName } = this.selectedKey()
    // 合同变更 - 部分还款 移出来，后端不好处理，前端特殊处理。
    if (['CHANGE_UNCOMMIT', 'CHANGE_COMMIT'].includes(contractProcessStatus) && contractProcessName.includes('提前还款')) {
      message.info('当前合同流状态不允许发起该操作')
      return
    }

    this.changeCheck(['CHANGE_UNCOMMIT', 'CHANGE_COMMIT'], 'change', () => {
      if (this.availAbleType === 'OTHER') {
        history.push(`/contract/list/detail/${id}?formChangeOther=true&bizType=${bizType}`)
      } else {
        history.push(`/contract/list/change/${id}?changeType=${this.availAbleType}&bizType=${bizType}`)
      }
    })
  }

  // 合同起租
  onContractStartRent = () => {
    this.checkSelected()
    const { id, bizType } = this.selectedKey()
    this.changeCheck(['START_RENT_UNCOMMIT', 'START_RENT_CANCEL'], 'rent', () => {
      this.contractOptPre(contractOperationMap.START_RENT, () => {
        history.push(`/contract/list/startRent/${id}?bizType=${bizType}`)
      })
    })
  }
  // 新增投放
  onCreateReceipt = () => {
    this.checkSelected()
    const { id, bizType } = this.selectedKey()
    this.changeCheck(['NEW_RECEIPT_UNCOMMIT', 'NEW_RECEIPT_COMMIT'], 'receipt', () => {
      this.contractOptPre(contractOperationMap.NEW_RECEIPT, () => {
        history.push(`/contract/list/createReceipt/${id}?bizType=${bizType}`)
      })
    })
  }
  // 提前还款
  onContractPrepayment = async () => {
    this.checkSelected()
    const { id, bizType, contractProcessStatus, contractProcessName } = this.selectedKey()
    // 合同变更 - 部分还款 移出来，后端不好处理，前端特殊处理。
    if (['CHANGE_UNCOMMIT', 'CHANGE_COMMIT'].includes(contractProcessStatus) && !contractProcessName.includes('提前还款')) {
      message.info('当前合同流状态不允许发起该操作')
      return
    }
    this.changeCheck(['CHANGE_UNCOMMIT', 'CHANGE_COMMIT'], 'prepayment', () => {
      this.contractOptPre('CHANGE_REPAYMENT_IN_ADVANCE', () => {
        history.push(`/contract/list/change/${id}?changeType=EARLY_REPAYMENT&bizType=${bizType}`)
      })
    })
  }
  // 合同结清
  onContractSettlement = () => {
    this.checkSelected()
    const settleType = 'SETTLE_NORMAL'
    const { id, bizType, contractProcessStatus } = this.selectedKey()
    // SETTLE_UNCOMIIT
    this.contractOptPre(contractOperationMap[settleType], () => {
      history.push(`/contract/list/settlement/${id}?planType=${settleType}&bizType=${bizType}`)
    })
  }
  // 保证金退抵
  onMarginRefund = async () => {
    this.checkSelected()
    const settleType = 'SETTLE_NORMAL'
    const { id, bizType, clientId, clientName, contractProcessStatus } = this.selectedKey()
    // if (
    //   ['NEW_UNCOMMIT','CHANGE_UNCOMMIT', 'CHANGE_COMMIT'].includes(contractProcessStatus)
    // ) {
    //   message.info('当前合同流状态不允许发起该操作')
    //   return
    // }
    try {
      const businessKey = await Api.marginRefundCheck({ contractId: id })
      // 该合同保证金余额不足/存在在途保证金退抵/合同提前结清/合同结清流程，请等待结束后操作
      history.push(`/contract/list/marginRefund/${id}?businessKey=${businessKey}&planType=${settleType}&bizType=${bizType}&first=1&clientName=${clientName}`)
    } catch (e) {}
  }
}
export default new Store()

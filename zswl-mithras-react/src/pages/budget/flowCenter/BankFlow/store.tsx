import {
  DrawerStore,
  FormStore,
  Modal,
  ModalStore,
  PageStore,
  TableStore,
  Table,
} from '@zswl/components'
import { makeAutoObservable, toJS } from '@zswl/admin'
import bankFlowProcessingCenterApi from '@/api/budget/flowCenter/bankFlowProcessingCenterApi'
import { message } from 'antd'
import _, { uniqueId } from 'lodash'
import { calcThisAmount } from './WriteOffDrawer'
import bankFlowCapitalApi from '@/api/budget/flowCenter/bankFlowCapitalApi'
import moment from 'moment'
import { isFundDept, isFinicalDept, timeFormat } from '@/utils'
import thirdCanqiongApi from '@/api/budget/flowCenter/thirdCanqiongApi'
import { formatModalData, formatListJson, formatOffModal, absColumns } from './utils'
import { validateModal } from '@/utils/modal'

class Store {
  constructor({ type }) {
    makeAutoObservable(this)
    this.type = type
  }
  type = undefined
  page = new PageStore({
    request: (params) => {},
  })
  handleOff = async () => {
    const { keys } = this.table.getSelected()
    await bankFlowProcessingCenterApi.postProcessingRequire({ financeFlowIds: keys })
    message.success('处理成功')
    this.clearSelected()
    this.table.search()
  }
  table = new TableStore({
    request: async (params) => {
      const newParams = {
        ...params,
        tabType: this.type,
      }
      if (this.type === 'PROCESSING_CENTER_WRITE') {
        newParams.tabType = 'PROCESSING_CENTER'
        newParams.collectionPaymentType = 'COLLECTION'
      }
      const res = await bankFlowProcessingCenterApi.postCenterList(newParams)
      return res
    },
  })

  groupBy = (list, needFormat = true, defaultList = []) => {
    list.forEach((params) => {
      const isFund = this.sideType === 'FUNDS_END'
      const newParams = needFormat ? formatModalData(params, isFund) : params
      // 本来要聚合的现在不用了，名字懒得改了
      defaultList.push(newParams)
    })
    this.cashFlowTable.setList(defaultList)
  }

  offModal = new ModalStore({
    onFinish: async (params) => {
      const record = this.offModal.getInitialValues()
      // 资金端 付款 利息的情况下不需要校验
      const notValid =
        this.sideType === 'FUNDS_END' &&
        this.writeOffType === 'PAYMENT' &&
        params.cashFlowItem.value === 'INTEREST'
      if (!notValid && params?.thisWriteOffAmount > params.noPayAmount) {
        message.error('本次核销金额大于未收/未付金额，请重新输入')
        return
      }
      const old = this.cashFlowTable.getList()
      // 编辑的时候考虑到子节点
      if (record?.editType === 'EDIT') {
        const isFund = this.sideType === 'FUNDS_END'
        const newParams = formatModalData(params, isFund)
        const groupFiled = isFund ? 'orgId' : 'clientId'

        const find = old.find((item) => item[groupFiled] === newParams[groupFiled])
        if (find.uuid === record.uuid) {
          find.thisWriteOffAmount = newParams.thisWriteOffAmount
          find.writeOffedAmount = this.writeOffedAmount
        }
        this.cashFlowTable.setList(old)
      } else {
        // 新增的时候直接调用就行
        const old = this.cashFlowTable.getList()
        this.groupBy(
          [{ ...params, writeOffedAmount: this.writeOffedAmount, uuid: uniqueId() }],
          true,
          old
        )
      }
      this.offModal.close()
    },
  })

  // 收付款类型===“付款” && 核销类型===“资金端”
  isPaymentAndFundEnd = false
  setIsPaymentAndFundEnd = (val) => {
    this.isPaymentAndFundEnd = val
  }
  filterParams = {}
  specialOffModal = new ModalStore({
    onFinish: async (values) => {
      const { actualLoanDate, receiptRepayBaseIdList } = values
      const params = {
        actualLoanDateFrom: moment(actualLoanDate[0]).format('yyyy/MM/DD'),
        actualLoanDateTo: moment(actualLoanDate[1]).format('yyyy/MM/DD'),
        receiptRepayBaseIdList,
      }
      this.filterParams = params
      this.specialOffModal.close()
      this.specialPayFlowListModal.open()
    },
  })

  specialPayFlowListModal = new ModalStore({
    onFinish: async (values) => {},
  })

  specialWriteOffResultModal = new ModalStore({
    onOpen: () => {
      const { rows } = this.table.getSelected()
      setTimeout(() => {
        this.bankFlowTable.setList(rows)
        this.tableChange()
        this.specialWriteOffResultTable.search()
        this.clearSelected()
      }, 0)
    },
    onFinish: async (values) => {},
  })
  specialWriteOffResultTable = new TableStore({
    pagination: false,
    request: async (values) => {
      const bankFlowTableData = this.bankFlowTable.getList()
      const financingFlowIdList = bankFlowTableData.map((item) => item.id)
      if (!financingFlowIdList.length) return []
      const res = await bankFlowCapitalApi.postSubList({
        bankFlowIds: financingFlowIdList,
      })
      const newTableData = (res ?? []).map((v) => ({ ...v, isRemote: true, uuid: uniqueId() }))
      return newTableData
    },
  })
  sumData = {}
  remainingDetailList = []
  specialPayFlowListTable = new TableStore({
    pagination: false,
    request: async (values) => {
      const { list, shouldPayAmountSum, noPayAmountSum, remainingDetailList } =
        await bankFlowCapitalApi.postCashFlowList({
          ...this.filterParams,
        })
      this.sumData = { shouldPayAmount: shouldPayAmountSum, noPayAmount: noPayAmountSum }
      this.remainingDetailList = remainingDetailList
      return list
    },
  })

  submitWriteOff = async (list, isAuto = 0, repaySplitInfoList) => {
    const financeFlowIds = this.bankFlowTable.getList().map((item) => item.id)
    try{
      await bankFlowCapitalApi.postPaymentWriteoff({
            financeFlowIds,
            listDataJson: JSON.stringify(list),
            isAuto,
            repaySplitInfoList,
      })
    }catch(error){
      if("本次核销后剩余本金不为0，请核对！"==error){
           Modal.confirm({
               title: '提示',
               content: error,
               onOk: async () => {
                   message.success('操作成功')
                   this.specialPayFlowListModal.close()
                   this.offDrawer.close()
                   this.specialWriteOffResultModal.open()
               },
           })
         return
      }else{
        return
      }
    }
    message.success('操作成功')
    this.specialPayFlowListModal.close()
    this.offDrawer.close()
    this.specialWriteOffResultModal.open()
  }

  addCashFlow = () => {
    const { writeOffType, sideType } = this.form.getFieldsValue(true)
    const isPaymentAndFundEndMode = writeOffType === 'PAYMENT' && sideType === 'FUNDS_END'
    this.setIsPaymentAndFundEnd(isPaymentAndFundEndMode)
    if (isPaymentAndFundEndMode) {
      this.specialOffModal.open()
    } else {
      this.offModal.open()
    }
  }
  sideType = undefined
  setSideType = async (type) => {
    this.sideType = type
    if (type === 'NO_PROCESSING_REQUIRE') return
    const bankFlowTableData = this.bankFlowTable.getList()
    const financingFlowIdList = bankFlowTableData.map((item) => item.id)
    const func =
      type === 'FUNDS_END'
        ? bankFlowCapitalApi.postSubList
        : bankFlowProcessingCenterApi.postSubList
    const field = type === 'FUNDS_END' ? 'bankFlowIds' : 'financingFlowIdList'
    const res = await func({ [field]: financingFlowIdList })
    const newTableData = (res ?? []).map((v) => ({ ...v, isRemote: true, uuid: uniqueId() }))

    setTimeout(() => {
      this.groupBy(newTableData, false)
    }, 10)
  }
  writeOffType = null
  setWriteOffType = (type) => {
    this.writeOffType = type
  }
  bankFlowDefault = []
  tableChange = async () => {
    const bankFlowTableData = this.bankFlowTable.getList()

    const amount = (bankFlowTableData ?? []).reduce((pre, cur) => {
      return pre + (cur.paymentAmount - cur.collectionAmount)
    }, 0)
    const writeOffType = amount > 0 ? 'PAYMENT' : 'COLLECTION'
    const amountTotal = bankFlowTableData.reduce((pre, cur) => {
      return pre + calcThisAmount(cur, writeOffType)
    }, 0)
    this.form.setFieldValue('amount', Math.abs(amountTotal))
    this.writeOffType = writeOffType
    this.form.setFieldValue('writeOffType', writeOffType)
    const isSide = ['PROCESSED_PROJ_SIDE', 'PROCESSED_FUNDS_END'].includes(this.type)
    //登录用户为资金部的用户，核销类型默认值为：资金端； 登录用户为财务部的用户，核销类型默认值为：项目端。 可修改。
    let sideTypeValue = null
    if (isSide) {
      sideTypeValue = this.type === 'PROCESSED_PROJ_SIDE' ? 'PROJ_SIDE' : 'FUNDS_END'
    } else if (isFundDept()) {
      sideTypeValue = 'FUNDS_END'
    } else if (isFinicalDept()) {
      sideTypeValue = 'PROJ_SIDE'
    }

    this.form.setFieldValue('sideType', sideTypeValue)
    this.setSideType(sideTypeValue)
  }
  writeOffedAmount = []
  setWriteOffedAmount = (data) => {
    this.writeOffedAmount = data
  }
  handleSubmit = async () => {
    const financeFlowIds = this.bankFlowTable.getList().map((item) => item.id)
    if (this.sideType === 'NO_PROCESSING_REQUIRE') {
      await this.handleReverse()
      await bankFlowProcessingCenterApi.postProcessingRequire({ financeFlowIds })
      message.success('处理成功')
      this.offDrawer.close()
      this.table.search()
      this.clearSelected()

      return
    }
    const newListDataJson = formatListJson(this.cashFlowTable.getList())
    const total = newListDataJson.reduce((pre, cur) => {
      return pre + +cur.thisWriteOffAmount
    }, 0)
    const amount = this.form.getFieldValue('amount')

    if (total > amount) {
      message.error('本次核销金额不能大于核销总金额')
      return
    }

    const listDataJson = JSON.stringify(newListDataJson)
    await this.handleReverse()
    if (newListDataJson.length) {
      await bankFlowProcessingCenterApi.postWriteOff({
        financeFlowIds,
        sideType: this.sideType,
        writeOffType: this.writeOffType,
        listDataJson,
      })
    }

    message.success('核销成功')
    this.offDrawer.close()
    this.table.search()
    this.clearSelected()
  }
  offDrawer = new DrawerStore({
    onOpen: () => {
      const { rows } = this.table.getSelected()
      setTimeout(() => {
        this.bankFlowTable.setList(rows)
        this.tableChange()
      }, 0)
    },
  })
  bankFlowTable = new TableStore()
  bankTableDelete = (id) => {
    this.bankFlowTable.deleteRow(id)
    this.tableChange()
  }
  form = new FormStore()
  modalForm = new FormStore()
  delete = async (financeFlowIds) => {
    await bankFlowProcessingCenterApi.postCenterDelete({ financeFlowIds })
    message.success('删除成功')
    this.table.search()
  }
  restore = async (financeFlowIds) => {
    Modal.confirm({
      title: '还原',
      content: `还原选中的${financeFlowIds?.length}条记录`,
      onOk: async () => {
        await bankFlowProcessingCenterApi.postBankCenterRestore({ ids: financeFlowIds })
        message.success('操作成功')
        this.table.search()
      },
    })
  }
  pullFlow = async () => {
    const res = await bankFlowProcessingCenterApi.postPullFlow({})
    if (res.length) {
      Modal.info({
        title: '提示',
        content: `交易明细编号为${res?.join('、')}的流水，融租易系统未删除，请及时处理！`,
      })
    }
    message.success('拉取成功')
    this.table.search()
    this.clearSelected()
  }
  cashFlowTable = new TableStore()

  handleOk = async (record, index) => {
    this.offModal.open(record)
  }

  deleteRow = (record, index) => {
    const formatList = formatListJson(this.cashFlowTable.getList())
    const newList = (formatList ?? []).filter((item) => item.uuid !== record.uuid)
    this.groupBy(newList, false, [])
  }

  handleEdit = (record) => {
    const isFund = this.sideType === 'FUNDS_END'
    const newRecd = formatOffModal(record, isFund)
    this.offModal.open(newRecd)
    this.setWriteOffedAmount(record.writeOffedAmount)
  }
  diffRefundModal = new ModalStore({
    onFinish: async (values, initialValues) => {
      const { rows } = this.table.getSelected()
      await bankFlowProcessingCenterApi.postNettingRefund({
        financeFlowIds: rows.map((item) => item.id),
        refundAmount: values.refundAmount,
      })
      message.success('操作成功')
      this.diffRefundModal.close()
      this.table.search()
      this.clearSelected()
    },
  })
  diffRefund = async () => {
    const { rows } = this.table.getSelected()
    if (!rows.length) {
      message.info('请同时勾选预收流水 与 退款流水，进行轧差')
      return
    }
    this.diffRefundModal.open()
  }
  confirmIncomeModal = new ModalStore({
    onFinish: async ({ writeOffAmount, receiptId, cashFlowItem, contract, client }) => {
      const { rows } = this.table.getSelected()
      await bankFlowProcessingCenterApi.postConfirmIncome({
        cashFlowItem,
        receiptId,
        writeOffAmount,
        financeFlowId: rows[0].id,
        clientId: client.value,
        contractId: contract.value,
      })
      message.success('操作成功')
      this.confirmIncomeModal.close()
      this.table.search()
    },
  })
  confirmIncome = () => {
    const { rows } = this.table.getSelected()
    if (rows.length !== 1) {
      message.info('请选中1条操作')
      return
    }
    this.confirmIncomeModal.open()
  }
  handleSelectChange = (selectedRowKeys, selectedRows) => {
    this.table.setSelected(selectedRowKeys, selectedRows)
    this.setSelectedRecord(selectedRowKeys, selectedRows)
  }
  selectedRecord = {
    selectedRows: [],
    selectedRowKeys: [],
  }
  setSelectedRecord = (keys = [], rows = []) => {
    this.selectedRecord = {
      selectedRows: rows,
      selectedRowKeys: keys,
    }
  }
  clearSelected = (needMessage = false) => {
    this.table.clearSelected()
    this.setSelectedRecord()
    if (needMessage) message.success('已全部清空')
  }

  handleReverse = async () => {
    // this.absTable.search({ cashFlowCodeList })

    const list = this.cashFlowTable.getList()
    const confirmList = list.filter((v) => v.isConfirm)
    if (confirmList.length === 0) return
    const hasABSABN = confirmList.some((v) => ['ABS', 'ABN'].includes(v.businessType))
    const reverseReq = async (directRepaySplitList = []) => {
      const params = confirmList.map(({ id: businessKey, source, platform }) => ({
        platform,
        source,
        businessKey,
        directRepaySplitList,
      }))
      const res = await thirdCanqiongApi?.postFinancialWithdraw(params)
      // status或 success 两个都为 false 才视为失败
      const errList = res.filter((v) => !(v.status || v.success))
      if (errList.length > 0) {
        errList.forEach((element) => {
          message.error(element.message)
        })
      }
    }

    if (hasABSABN) {
      const cashFlowCodeList = confirmList.map((v) => v.cashFlowCode)
      setTimeout(() => {
        this.absTable.search({ cashFlowCodeList })
        // this.absTable.setList(mockData)
      }, 100)
    }
    const res = await validateModal(
      {
        title: 'ABS/ABN产品核销明细',
        width: 1200,
        content: (
          <Table store={this.absTable} columns={absColumns} editable scroll={{ x: 'auto' }} />
        ),
        onOk: async () => {
          const { list } = await this.absTable.submit()
          const repaySplitInfoList = list.map(({ id, amount }) => {
            return {
              id,
              amount: amount ? amount * 10000 : 0,
            }
          })
          return repaySplitInfoList
        },
      },
      hasABSABN
    )
    await reverseReq(res)
  }
  // 反核销
  reverse = async (record) => {
    this.cashFlowTable.setRow(record.uuid, { ...record, isConfirm: true })
  }

  absTable = new TableStore({
    pagination: false,
    request: async (values) => {
      if (!values?.cashFlowCodeList?.length) return []
      const res = await bankFlowProcessingCenterApi.postFinanceRepaySplitWriteoffList(values)
      return res
    },
  })
}
export default Store

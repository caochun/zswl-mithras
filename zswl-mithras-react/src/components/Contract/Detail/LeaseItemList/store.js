import { makeAutoObservable } from '@zswl/admin'
import { TableStore, ModalStore, Modal } from '@zswl/components'
import { message } from 'antd'
import DataUpload from '@/components/DataUpload'
import Api from './api'
import { downFile } from '@/utils'

class Store {
  constructor(data) {
    this.isFormApproval = data?.isFormApproval
    this.contractId = data?.contractId
    this.businessVersion = data?.businessVersion
    this.flowId = data?.flowId
    makeAutoObservable(this)
  }

  formatList = (value) => {
    const { list, ...rest } = value
    const result = []
    list?.map((item) => {
      const obj = {}
      item.dataList?.map((i) => {
        obj[i.key] = i.value
      })

      result.push({
        ...obj,
        id: item.itemId,
        canChoose: item.canChoose,
        hasChoose: item.hasChoose,
      })
    })
    return {
      list: result,
      ...rest,
    }
  }
  // 动态标题列
  headerList = []
  setHeaderList = (value) => {
    this.headerList = value
  }
  // 租赁成本
  totalCost = ''
  setTotalCost = (value) => {
    this.totalCost = value
  }
  // 租赁物总额
  totalAmount = ''
  setTotalAmount = (value) => {
    this.totalAmount = value
  }

  tableData = {}
  changeFlag = false
  $table = new TableStore({
    request: async (params) => {
      if (this.isFormApproval) {
        const result = await Api.postListCompare({
          ...params,
          contractId: this.contractId,
          businessVersion: this.businessVersion,
        })
        this.changeFlag = result.changeFlag
      }
      const result = await Api.postList({
        contractId: this.contractId,
        businessVersion: this.businessVersion,
        ...params,
      })
      if (!result.headerList || result.headerList?.length === 0) return []
      this.setHeaderList(result.headerList)
      this.setTotalCost(result.totalCost)
      this.setTotalAmount(result.leaseItemTotalAmount)
      return this.formatList(result.pageList)
    },
  })

  // 租赁物总额保存
  saveTotalAmount = async (value) => {
    await Api.postLeaseitemTotalAmountSave({
      contractId: this.contractId,
      leaseItemTotalAmount: value,
    })
    message.success('保存成功')
  }

  // 批量导出
  batchExport = async () => {
    const { keys } = this.$table.getSelected()
    const res = await Api.postBatchExport({ contractId: this.contractId, itemIds: keys })
    const { msg } = res ?? {}
    msg && message.info(msg)
  }

  // 模版下载
  downloadTemplate = async () => {
    const res = await Api.postDownloadTemp({ contractId: this.contractId })
    downFile(res)
  }

  // 引入租赁物
  checkInprocess = async () => {
    await Api.postLeaseItemInprocessCheck({ contractId: this.contractId })
    this.processLeaseModal.open()
  }

  // 导入
  onFileChange = async (file) => {
    const { fileList } = DataUpload.classify(file)
    await Api.postUploadData({
      file: fileList[0],
      contractId: this.contractId,
    })
    this.$table.search({ contractId: this.contractId })
  }

  // 选择租赁物-弹窗
  processLeaseModal = new ModalStore({
    onOpen: async () => {
      return {}
    },
    onFinish: async () => {
      modal.close()
      this.processLeaseTable.setSelected([])
    },
  })

  // 选择租赁物-头部
  chooseHeader = []
  setChooseHeader = (data) => {
    this.chooseHeader = data
  }
  // 租赁物审核管理id
  leaseItemInfoId = ''
  // 已审核通过的租赁物，默认勾选
  selectedRowKeys = []
  // 选择租赁物-表格
  processLeaseTable = new TableStore({
    pagination: {
      pageSizeOptions: [10, 50, 100, 500],
    },

    request: async (params) => {
      const result = await Api.postLeaseItemPrechoose({
        ...params,
        contractId: this.contractId,
      }).catch((err) => {
        this.processLeaseTable.setLoading(false)
        return []
      })
      this.leaseItemInfoId = result.leaseItemInfoId
      this.setChooseHeader(result.headerList)
      const tableList = this.formatList(result.pageList)
      this.resetCurPageSelected()
      return tableList
    },
  })

  // 分页记住选中
  cacheChooseId = []
  setCacheChooseId = (data) => {
    this.cacheChooseId = data
  }
  resetCurPageSelected = () => {
    setTimeout(() => {
      this.processLeaseTable.setSelected(this.cacheChooseId)
    })
  }
  onChooseSelect = (record, selected, selectedRow) => {
    let keys = [...this.cacheChooseId]
    if (selected) {
      keys = [...this.cacheChooseId, record.id]
    } else {
      keys = this.cacheChooseId.filter((item) => item !== record.id)
    }
    this.setCacheChooseId(keys)
  }
  onChooseSelectAll = (selected, selectedRows, changeRows) => {
    if (selected) {
      const addCheckedKeys = changeRows.map((item) => {
        return item.id
      })
      this.setCacheChooseId([...this.cacheChooseId, ...addCheckedKeys])
    } else {
      const subCheckedKeys = this.cacheChooseId.filter((id) => {
        return !changeRows.some((item) => {
          return item.id === id
        })
      })
      this.setCacheChooseId(subCheckedKeys)
    }
  }
  cancelChooseSelected = () => {
    this.setCacheChooseId([])
    this.processLeaseTable.setSelected([])
  }

  // 选择租赁物-确定
  onProcessSelect = async () => {
    // const { keys } = this.processLeaseTable.getSelected()
    const keys = this.cacheChooseId
    await Api.postLeaseItemChoose({
      leaseItemInfoId: this.leaseItemInfoId,
      contractId: this.contractId,
      itemIds: keys,
    })
    message.success('操作成功')
    this.processLeaseTable.search()
    this.processLeaseModal.close()
    this.$table.search()
  }
  // 变更租赁物
  changeLease = () => {
    Modal.confirm({
      title: `确认变更租赁物清单？`,
      onOk: async () => {
        await Api.postReviewModifyEffect({
          contractId: this.contractId,
          flowId: this.flowId,
        })
        message.success('操作成功')
      },
    })
  }
  // 存在存量项目，变更租赁物
  changeLease2 = () => {
    Modal.confirm({
      title: '提示',
      content: (
        <div>
          <div>
            此项目为存量项目，点击确定后将发起租赁物创建流程，可在【我的流程-我收到的】维护项目租赁物信息后提交审批。
          </div>
          <br />
          <div>请在租赁物创建流程审批通过后再维护此模块租赁物信息！</div>
        </div>
      ),
      onOk: async () => {
        await Api.postReviewModifyEffect({
          contractId: this.contractId,
          flowId: this.flowId,
        })
        message.success('操作成功')
      },
    })
  }
}
export default Store

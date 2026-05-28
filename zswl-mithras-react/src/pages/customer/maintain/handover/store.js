import { timeFormat } from '@/utils'
import { history, makeAutoObservable } from '@zswl/admin'
import { ModalStore, PageStore, TableStore } from '@zswl/components'
import { message } from 'antd'
import localApi from './api'

class Store {
  constructor(initData) {
    makeAutoObservable(this)
  }

  page = new PageStore({
    request: async ({ id, isFormApproval }) => {
      let batchNoInfo = {}
      if (isFormApproval) {
        batchNoInfo = await localApi.postTransferApplyQueryByBatchNo({ batchNo: id })
        const res = await localApi.postClientNewTransferDetail({
          batchNo: id,
        })
        return {
          ...res,
          batchNoInfo,
        }
      }
      batchNoInfo = await localApi.postTransferApplyCreate({})
      return {
        ...this.page.getParams(),
        batchNoInfo,
      }
    },
  })

  table = new TableStore({
    pagination: false,
    request: async () => {
      const { belongSponsorId, belongDeptId, isFormApproval } = this.page.getParams()
      if (isFormApproval) {
        const res = this.page.getData().sponsorClientNewList
        // 默认展开所有
        this.setExpandKeys(res?.map((item, index) => `${item.id}`))
        return res
      } else {
        const res = await localApi.postClientMewListBySponsors({
          belongSponsorId,
          belongDeptId,
        })
        return res
      }
    },
  })
  expandKeys = []
  setExpandKeys = (data) => {
    this.expandKeys = data
  }
  successModal = new ModalStore({})

  handleCancel = async () => {
    const { belongSponsorId, belongDeptId } = this.page.getParams()
    await localApi.postNewTransferRemove({ belongSponsorId, belongDeptId })
    message.success('操作成功', 2, () => {
      history.push('/customer/maintain')
    })
  }
  handleSubmit = async (values) => {
    const { description, transferDate } = values
    const { belongSponsorId, belongDeptId } = this.page.getParams()
    const { batchNoInfo } = this.page.getData()

    const { rows } = this.table.getSelected()
    if (rows.length === 0) {
      message.info('请选择移交客户')
      return
    }
    const transferClientList = rows.map((item) => {
      return {
        ...item,
        clientId: item.id,
      }
    })

    await localApi.postClientNewTransferSubmit({
      belongSponsorId,
      belongDeptId,
      transferDate: timeFormat(transferDate),
      description,
      transferClientList,
      batchNo: batchNoInfo.batchNo,
    })

    this.successModal.open()
    message.success('操作成功')
  }

  export = async () => {
    const { keys } = this.table.getSelected()
    const { belongDeptId, belongSponsorId } = this.page.getData()
    const { isFormApproval } = this.page.getParams()
    const { batchNoInfo } = this.page.getData()

    await localApi.postClientTransferExport({
      clientIds: keys,
      belongDeptId,
      belongSponsorId,
      batchNo: isFormApproval ? batchNoInfo.batchNo : undefined,
    })
  }

  editStatus = true
  setEditStatus = (flag) => {
    this.editStatus = flag
  }

  editSponsorModal = new ModalStore({
    onOpen: (values) => {
      return {
        ...values,
      }
    },
  })

  // 批量编辑相关
  selectedProjectRows = []
  selectedProjectKeys = []

  setSelectedProjectRows = (rows) => {
    this.selectedProjectRows = rows
  }

  setSelectedProjectKeys = (keys) => {
    this.selectedProjectKeys = keys
  }

  // 切换单个项目选择
  toggleProjectSelection = (record, selected, uniqueKey) => {
    if (selected) {
      if (!this.selectedProjectKeys.includes(uniqueKey)) {
        this.selectedProjectKeys = [...this.selectedProjectKeys, uniqueKey]
        this.selectedProjectRows = [
          ...this.selectedProjectRows,
          { ...record, _uniqueKey: uniqueKey },
        ]
      }
    } else {
      this.selectedProjectKeys = this.selectedProjectKeys.filter((k) => k !== uniqueKey)
      this.selectedProjectRows = this.selectedProjectRows.filter((r) => r._uniqueKey !== uniqueKey)
    }
    // 同步父表格选中状态
    this.syncParentSelection(record.id)
  }

  // 切换多个项目选择（用于全选/反选）
  toggleProjectsSelection = (items, selected) => {
    if (selected) {
      // 添加不存在的
      const newKeys = [...this.selectedProjectKeys]
      const newRows = [...this.selectedProjectRows]

      items.forEach(({ record, uniqueKey }) => {
        if (!newKeys.includes(uniqueKey)) {
          newKeys.push(uniqueKey)
          newRows.push({ ...record, _uniqueKey: uniqueKey })
        }
      })
      this.selectedProjectKeys = newKeys
      this.selectedProjectRows = newRows
    } else {
      // 移除存在的
      const keysToRemove = items.map((i) => i.uniqueKey)
      this.selectedProjectKeys = this.selectedProjectKeys.filter((k) => !keysToRemove.includes(k))
      this.selectedProjectRows = this.selectedProjectRows.filter(
        (r) => !keysToRemove.includes(r._uniqueKey)
      )
    }
    // 同步父表格选中状态（根据 items 中的 clientId 去重后逐个检查）
    const clientIds = [...new Set(items.map((i) => i.record.id))]
    clientIds.forEach((clientId) => this.syncParentSelection(clientId))
  }

  // 检查并同步父表格选中状态
  syncParentSelection = (clientId) => {
    const tableData = this.table.getList() || []
    const clientRecord = tableData.find((item) => item.id === clientId)
    if (!clientRecord || clientRecord.inProcess) return

    const projects = clientRecord.clientProjRSPList || []
    // 过滤掉 inProcess 的项目
    const selectableProjects = projects.filter((p) => !p.inProcess)

    if (selectableProjects.length === 0) return

    // 检查所有可选项目是否都被选中
    const allSelected = selectableProjects.every((p) => {
      const index = projects.indexOf(p)
      const uniqueKey = `${clientId}_${p.id}_${index}`
      return this.selectedProjectKeys.includes(uniqueKey)
    })

    // 同步父表格选中状态
    const { keys: currentKeys = [], rows: currentRows = [] } = this.table.getSelected() || {}
    const clientKey = `${clientId}`

    if (allSelected && !currentKeys.includes(clientKey)) {
      // 所有子项目都选中了，选中父行
      this.table.setSelected([...currentKeys, clientKey], [...currentRows, clientRecord])
    } else if (!allSelected && currentKeys.includes(clientKey)) {
      // 有子项目未选中，取消父行选中
      this.table.setSelected(
        currentKeys.filter((k) => k !== clientKey),
        currentRows.filter((r) => `${r.id}` !== clientKey)
      )
    }
  }

  // 客户维度选择联动
  toggleClientSelection = (clientRecord, selected) => {
    const projects = clientRecord.clientProjRSPList || []
    const items = projects.map((p, index) => ({
      record: { ...p, id: clientRecord.id },
      uniqueKey: `${clientRecord.id}_${p.id}_${index}`,
    }))
    this.toggleProjectsSelection(items, selected)
  }

  batchEditModal = new ModalStore({
    onOpen: () => {
      // 计算是否包含空编号
      const hasEmptyCode = this.selectedProjectRows.some((p) => !p.projCode)
      return {
        hasProjCodeContractCode: !hasEmptyCode,
      }
    },
  })

  openBatchEdit = () => {
    if (this.selectedProjectRows.length === 0) {
      message.info('请选择待移交客户！') // 需求提示语：请选择待移交客户！(虽然实际选的是项目)
      return
    }
    this.batchEditModal.open()
  }

  handleBatchSave = async (values) => {
    const { belongSponsorId, processInstanceId } = this.page.getParams()
    const { batchNoInfo } = this.page.getData()
    const { toCosponsorIds, toCosponsorNames } = values

    const rows = this.selectedProjectRows
    const formattedRows = rows.map((row) => {
      return {
        ...row,
        ...values,
        toCosponsorIds: toCosponsorIds,
        toCosponsorNames: toCosponsorIds?.length > 0 ? toCosponsorNames : '',
        transferWeightId: row.transferWeightId,
        belongSponsorId,
        processInstanceId,
        batchNo: batchNoInfo.batchNo,
      }
    })
    try {
      await localApi.postClientMewTransferModifyBatch({
        sponsorClientModifyNewBatch: formattedRows,
      })

      message.success('保存成功')
      this.batchEditModal.close()
      await this.page.init()
      this.table.search()
      this.selectedProjectRows = []
      this.selectedProjectKeys = []
      this.table.clearSelected()
    } catch (error) {
      console.error(error)
      // message.error('保存失败')
    }
  }

  handelSaveSponsorInfo = async (values) => {
    const { belongSponsorId, processInstanceId } = this.page.getParams()
    const { toCosponsorIds, toCosponsorNames } = values
    const { batchNoInfo } = this.page.getData()
    await localApi.postClientMewTransferModify({
      ...values,
      toCosponsorIds: toCosponsorIds,
      toCosponsorNames: toCosponsorIds?.length > 0 ? toCosponsorNames : '',
      transferWeightId: this.editSponsorModal.getInitialValues().transferWeightId,
      id: this.editSponsorModal.getInitialValues().id,
      belongSponsorId,
      processInstanceId,
      batchNo: batchNoInfo.batchNo,
    })
    await this.page.init()
    this.table.search()
    this.editSponsorModal.close()
  }
  saveData = async (values) => {
    const { batchNoInfo } = this.page.getData()
    await localApi.postClientNewTransferDetailModify({
      ...values,
      batchNo: batchNoInfo.batchNo,
    })
    message.success('保存成功')
    this.page.init()
  }
}

export default Store

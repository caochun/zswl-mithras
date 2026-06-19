import { DrawerStore, ModalStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import { timeFormat } from '@/utils'
import { message } from 'antd'
import moment from 'moment'
import checkPlanApi from '@/api/afterLease/checkPlan'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  selectedKey = () => {
    const { rows } = this.$table.getSelected()
    if (rows.length > 0) {
      return rows[0]
    }
    return null
  }

  $table = new TableStore({
    request: (params) => {
      return checkPlanApi.postCheckPlanAssetStrategy(params)
    },
  })

  $editModal = new ModalStore({
    onOpen: (initValues) => {
      const { deadLine, termName, ...rest } = initValues ?? {}
      return {
        ...rest,
        term: termName,
        deadLine: deadLine ? moment(deadLine) : undefined,
      }
    },
    onFinish: async (values) => {
      const { deadLine, riskManagerId } = values
      const data = {
        ...values,
        riskManagerId: riskManagerId?.value,
        deadLine: deadLine ? timeFormat(deadLine) : undefined,
      }
      await checkPlanApi.postCheckPlanAssetStrategyModify(data)
      message.success('操作成功')
      this.$editModal.close()
      this.$table.search()
    },
  })

  // 编辑
  editPlan = () => {
    if (!this.selectedKey()) {
      message.info('选中项为空')
      return
    }
    const row = this.selectedKey()
    this.$editModal.open({
      ...row,
      riskManagerId:
        row.checkWay === 'SITE'
          ? {
              label: row.riskManagerName,
              value: row.riskManagerId,
            }
          : undefined,
    })
  }
  // 日志
  $updateDrawer = new DrawerStore({
    onOpen: (row) => {
      this.$updateTable.search({
        planId: row.planId,
      })
    },
  })

  $updateTable = new TableStore({
    request: (params) => {
      return checkPlanApi.postCheckChangeRecordList({
        ...params,
      })
    },
  })

  updateLog = async (record) => {
    this.$updateDrawer.open(record)
  }

  close = async (record) => {}
}
export default new Store()

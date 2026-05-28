import { makeAutoObservable } from '@zswl/admin'
import { TableStore, Modal } from '@zswl/components'
import { message } from 'antd'
import { hasValue, timeFormat } from '@/utils'
import Api from './api'
import moment from 'moment'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  lastReportTime
  table = new TableStore({
    request: async (params) => {
      const result = await Api.list({
        ...params,
        dataTime: params.dataTime ? moment(params.dataTime).format('yyyy-MM') + '-01' : undefined,
      })
      this.lastReportTime = result?.lastReportTime
      return result.dataList ?? []
    },
  })

  allSelect = {}
  getSelect = async () => {
    Promise.all([Api.allSelect(), Api.pullDownList()]).then((res) => {
      const pullDownList = {}
      Object.keys(res[1]).map((key) => {
        const list = []
        res[1][key].map((item) => {
          list.push({
            value: item,
            label: item,
          })
        })
        pullDownList[key] = list
      })
      this.allSelect = {
        ...res[0],
        ...pullDownList,
      }
    })
  }

  batchReport = () => {
    const { dataTime } = this.table.getParams()
    Modal.confirm({
      title: `请确认是否批量报送?`,
      onOk: async () => {
        await Api.report({
          dataTime: moment(dataTime).format('yyyy-MM') + '-01',
        })
        message.success('报送成功！')
        this.table.search()
      },
    })
  }
  calc = () => {
    const { dataTime } = this.table.getParams()
    Modal.confirm({
      title: `请确认是否进行系统计算?`,
      onOk: async () => {
        await Api.calc({
          dataTime: moment(dataTime).format('yyyy-MM') + '-01',
        })
        message.success('操作成功！')
        this.table.search()
      },
    })
  }

  valNeedFormatUnit = ['WAN', 'YUAN', 'PERCENT', 'YI']

  // 列表编辑态索引
  editIndex = -1
  editItem = ({ rowIndex }) => {
    this.editIndex = rowIndex
  }
  cancelEdit = () => {
    this.editIndex = -1
  }

  confirmEdit = async ({ record }) => {
    const { values } = await this.table.submit()
    const editData = values[record.id]
    console.log({ editData })
    const { metricValueAdjusted } = editData
    await Api.save([
      {
        id: record.id,
        metricValueAdjusted:
          this.valNeedFormatUnit.includes(record.unit) && hasValue(metricValueAdjusted)
            ? String(metricValueAdjusted * 10000)
            : hasValue(metricValueAdjusted)
            ? String(metricValueAdjusted)
            : undefined,
      },
    ])
    message.success('保存成功！')
    this.editIndex = -1
    this.table.search()
  }
}
export default new Store()

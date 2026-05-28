import { TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import _ from 'lodash'

class Store {
  onChange: any
  beforeUpdate: (list, record, newData) => Promise<void>
  constructor({ onChange, beforeUpdate }) {
    this.onChange = onChange
    this.beforeUpdate = beforeUpdate
    makeAutoObservable(this)
  }
  table = new TableStore({
    pagination: false,
  })
  editIndex = -1
  setEditIndex = (index) => {
    this.editIndex = index
  }
  addRow = () => {
    const data = this.table.getList()
    this.setEditIndex(data.length)
    this.table.addRow({ isEdit: true }, 'last')
  }
  handleOk = async (record, index) => {
    const { values } = await this.table.submit()
    const recordId = record.id
    console.log('values: ', values, record)
    // 剔除 values 中的 record.id 字段
    const newObject = _.omit(values, [recordId])
    const newData = {
      ...record,
      ...values[recordId],
      ...newObject,
      isEdit: false,
    }
    const data = this.table.getList()
    const newList = _.cloneDeep(data)
    newList[index] = newData
    this.table.setRow(record.id, newData)
    await this.beforeUpdate?.(newList, record, newData)
    this.onChange?.(newList)
    this.setEditIndex(-1)
  }
  handleCancel = (record, index) => {
    this.setEditIndex(-1)
  }
  deleteRow = (record, index) => {
    const data = this.table.getList()
    const newList = _.cloneDeep(data)
    newList.splice(index, 1)
    this.table.setList(newList)
    this.onChange?.(newList)
  }
  handleEdit = (index) => {
    this.setEditIndex(index)
  }
}
export default Store

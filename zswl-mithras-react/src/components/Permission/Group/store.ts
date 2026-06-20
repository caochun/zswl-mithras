import { ModalStore, PageStore } from '@zswl/components'
import Api from '@/api/permission/groupManage'
import { message, Modal } from 'antd'
import { makeAutoObservable } from '@zswl/admin'
import _ from 'lodash'

const menuToTreeData = (data: any[], key?: string) => {
  return data.map((item, i) => {
    const { children, ...rest } = item
    const id = item?.groupId ?? item.menuId
    const newKey = key ? `${key}-${id}` : `${id}`
    return {
      key: newKey,
      disabled: !item.menuId,
      title: item?.groupName ?? item.menuName,
      children: children ? menuToTreeData(children, newKey) : [],
    }
  })
}
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async (params) => {
      const data = await Api.getMenuList({ id: params?.id })
      return { treeData: menuToTreeData(data) }
    },
  })
  modalTitle = '新增分组'
  groupModal = new ModalStore({
    onOpen: async (values) => {
      const id = values?.id
      if (!id) {
        this.modalTitle = '新增分组'
        await this.getFunctionList()
        return {}
      }
      this.modalTitle = '编辑分组'
      const { functions, ...rest } = await Api.getFunctionGroupDetail({ groupId: id })
      const functionIds = functions.filter((v) => v.hasPermission).map((v) => v.funcId)
      const newFunctionList = (functions || []).map(({ funcId: value, name: label }) => ({
        label,
        value,
      }))
      this.functionList = newFunctionList
      return { ...rest, functionIds }
    },
    onFinish: async (values, { id } = {}) => {
      if (id) {
        await Api.postFunctionGroupModify({
          id,
          ...values,
          menuId: this.menuId,
        })
      } else {
        await Api.postFunctionGroupAdd({ menuId: this.menuId, ...values })
      }
      this.groupModal.close()
      message.success('提交成功！')
      this.getGroupList(this.menuId)
    },
  })
  create = () => {
    if (!this.menuId) {
      message.info('请选择菜单')
      return
    }
    this.groupModal.open()
  }
  functionList = []
  getFunctionList = async () => {
    if (!this.menuId) {
      this.functionList = []
      return []
    }
    const data = (await Api.getFunctionGroupFunctions({ menuId: this.menuId })).map(
      ({ funcId: value, name: label }) => ({ label, value })
    )
    this.functionList = data || []
  }
  delete = async (id: number) => {
    Modal.confirm({
      title: '提示',
      content: '确定删除吗？',
      onOk: async () => {
        await Api.postFunctionGroupDelete({ id })
        message.success('删除成功')
        this.getGroupList(this.menuId)
      },
    })
  }
  edit = (id: number) => {
    this.groupModal.open({ id })
  }
  groupList = []
  menuId = null
  getGroupList = async (menuId) => {
    if (menuId) {
      const data = await Api.getFunctionGroupList({ menuId })
      this.menuId = menuId
      this.groupList = data
    } else {
      this.menuId = menuId
      this.groupList = []
    }
  }
  onCheck = async (checkedKeys) => {
    const menuId = _.last(checkedKeys?.[0]?.split('-'))
    this.getGroupList(menuId)
  }
}
export default new Store()

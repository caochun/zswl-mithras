import { TableStore, ModalStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import { message } from 'antd'
import assessmentWhitelistApi from '@/api/afterLease/assessmentWhitelistApi'

/**
 * 评估机构白名单管理数据存储类
 * 管理评估机构白名单页面的数据状态和业务逻辑
 */
class Store {
  constructor() {
    makeAutoObservable(this)
  }

  /**
   * 表格数据管理
   * 处理列表数据的获取和分页
   */
  $table = new TableStore({
    request: async (params) => {
      return assessmentWhitelistApi.postWhitelistPagelist({ ...params })
    },
  })

  /**
   * 新增模态框管理
   * 处理新增评估机构的弹窗逻辑
   */
  addModal = new ModalStore({
    onOpen: async () => {
      // 打开弹窗时的初始化逻辑
    },
    onFinish: async (values) => {
      const { creditCode: uscCode } = values
      const id = await assessmentWhitelistApi.postWhitelistAdd({ uscCode })
      message.success('操作成功')
      this.addModal.close()
      this.$table.search({ page: 1 })
      history.push(`/whiteList/detail/${id}`)
    },
  })

  /**
   * 处理新增评估机构
   * 打开新增弹窗
   */
  handleCreate = () => {
    this.addModal.open()
  }

  /**
   * 编辑模态框管理
   * 处理编辑评估机构的弹窗逻辑
   */
  editModal = new ModalStore({
    onOpen: async (record) => {
      // 打开编辑弹窗时加载数据
      this.currentRecord = record
    },
    onFinish: async (values) => {
      await assessmentWhitelistApi.postUpdate({ ...values, id: this.currentRecord.id })
      message.success('编辑成功')
      this.editModal.close()
      this.$table.search()
    },
  })

  /**
   * 处理删除评估机构
   * @param {Object} record - 要删除的记录
   */
  handleDelete = async (record) => {
    await assessmentWhitelistApi.postWhitelistDelete({ id: record.id })
    message.success('删除成功')
    this.$table.search()
  }
  handleChange = async () => {
    const record = this.$table.getSelected()?.rows[0]
    history.push(`/whiteList/detail/${record.id}?type=change`)
  }
  /**
   * 提交评估机构白名单出库申请
   * @param {Object} record - 要提交出库申请的记录
   */
  handleOutSubmit = async () => {
    const record = this.$table.getSelected()?.rows[0]
    history.push(`/whiteList/detail/${record.id}?type=out`)
  }

  /**
   * 跳转到详情页面
   * @param {Object} record - 要查看详情的记录
   */
  handleViewDetail = (record) => {
    history.push(`/whiteList/detail/${record.id}`)
  }
}

export default Store

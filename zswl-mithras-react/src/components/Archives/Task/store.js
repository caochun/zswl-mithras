import { TableStore, Modal, ModalStore, App, FormStore } from '@zswl/components'
import { makeAutoObservable, getRandomString } from '@zswl/admin'
import archiveTemplateApi from '@/api/archives/archiveTemplate'
import { cloneDeep } from 'lodash'
import { message } from 'antd'
class Store {
  constructor() {
    makeAutoObservable(this)
  }

  template = {}
  data = []
  disableAdd = true
  disableList = true
  isDetail = false
  isFold = []

  table = new TableStore({
    request: async (searchData) => {
      return await archiveTemplateApi.getList(searchData)
    },
  })

  editModal = new ModalStore({
    onOpen: async (id) => {
      if (id) {
        const dataModal = await archiveTemplateApi.detail({ templateId: id })
        dataModal.groups?.map(item => { item.disabled = true })
        this.template = dataModal
        this.isDetail = true
        this.disableList = false
        this.data = this.template.groups
        const arr = new Array(this.data?.length).fill(false)
        this.isFold = arr
        return dataModal
      } else {
        this.template = {}
        this.template.status = false
        this.data = []
        this.isDetail = false
      }
    },
  })

  form = new FormStore()

  setSwitch = (id) => {
    this.template = {
      ...this.template,
      groups: this.template.groups?.push({
        id: new Date().valueOf().toString(16),
        need: false,
      })
    }
    this.data?.map(item => {
      if (item.id === id) {
        item.disabled = false
      }
    })
    return this.data
  }

  //新增资料类型
  getFileInfo = () => {
    const groupName = this.form.getFieldValue('groupName')
    if (groupName) {
      //TODO
      const trimGroupName = groupName.trim()
      if (trimGroupName === '') {
        message.info('请删除资料类型名称中多余的空格！')
      } else {
        const temp = this.data?.filter(v => v.groupName === trimGroupName)
        if (temp?.length > 0) {
          message.info('资料类型名称不能重复！')
        } else {
          const len = this.data?.length
          const obj = {}
          obj.groupName = trimGroupName
          obj.id = len
          obj.disabled = true
          obj.items = []
          this.data.push(obj)
          this.setDisableAdd(true)
          this.disableList = false
        }
      }
    } else {
      message.info('请输入资料类型名称！')
    }
  }

  //新增文档类型
  setFileList = (id) => {
    const fileType = this.form.getFieldValue('fileType' + id)
    const need = this.form.getFieldValue('need' + id)
    if (fileType) {
      const trimFileType = fileType.trim()
      if (trimFileType === '') {
        message.info('请删除文档类型名称中多余的空格！')
      } else {
        this.data?.map(v => {
          if (v.id === id) {
            const obj = {
              id: getRandomString(8),
              fileType,
              need: need ? 1 : 0,
            }
            v.items.push(obj)
            v.disabled = true
          }
        })
      }
    } else {
      message.info('请输入文档类型！')
    }
  }

  //取消新增文档类型
  setCancel = (id) => {
    this.data?.map(v => {
      if (v.id === id) {
        v.disabled = true
      }
    })
  }

  //文档展开收起状态
  changeStyleStatus = (i, val) => {
    this.isFold[i] = val
  }

  setDisableAdd = (key) => {
    this.disableAdd = key
  }

  delModule = (id) => {
    this.data?.map((item, index) => {
      if (item.id === id) {
        this.data?.splice(index, 1)
      }
    })
  }

  delItem = (id, v) => {
    const cloneData = cloneDeep(this.data)
    cloneData?.map((key) => {
      if (key.id === id) {
        key.items?.map((i, j) => {
          if (i.fileType == v.fileType) {
            key.items?.splice(j, 1)
          }
        })
      }
    })
    this.data = cloneData
  }

  submit = async () => {
    const values = await this.form.submit()
    const { templateName, bizType, status } = values
    const cloneData = cloneDeep(this.data)
    const temp = cloneData?.filter(i => i.items?.length === 0)
    if (cloneData?.length && !temp?.length) {
      if (this.isDetail) {
        const statusDetail = this.template?.status
        if (statusDetail === status) {
          this.editModal.close()
        } else {
          const params = {
            templateId: this.template.templateId,
            bizType,
            status: status ? 'ENABLE' : 'DISABLED',
          }
          await archiveTemplateApi.update(params)
          message.success('更新成功！')
        }
      } else {
        const tempData = []
        cloneData?.map(v => {
          tempData.push({
            items: v.items,
            groupName: v.groupName
          })
        })
        const params = {
          templateName,
          bizType,
          status: status ? 'ENABLE' : 'DISABLED',
          groups: tempData
        }
        await archiveTemplateApi.add(params)
        message.success('添加成功！')
      }
      this.editModal.close()
      this.table.search()
    } else {
      message.info('请将资料内容填写完整后保存！')
    }
  }
}
export default new Store()

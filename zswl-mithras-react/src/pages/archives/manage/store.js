import { TableStore, FormStore, ModalStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import { downFile } from '@/utils'
import { message } from 'antd'
import archivesManageApi from '@/api/archives/manage'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  data = []
  expandedRowKeys = []
  setExpandedRowKeys = (keys) => {
    this.expandedRowKeys = keys
  }

  initLoop = (list) => {
    return list.map((item) => {
      const { archives, projName, id, files, groupKey, groupName, fileName, fileId, ...rest } = item
      const obj = {
        projName: projName || groupName || fileName,
        id: id || groupKey || fileId,
        key: id || groupKey || fileId,
        ...rest,
      }
      if (archives || files) {
        obj.children = this.initLoop(archives || files)
      }
      return obj
    })
  }

  loop = (list, parent = []) => {
    return list.map((item) => {
      const { children, projName, id, ...rest } = item
      const obj = {
        projName,
        id,
        ...rest,
        parent,
      }
      if (children) {
        obj.children = this.loop(children, parent.concat(id))
      }
      return obj
    })
  }

  changeKeys = (_data) => {
    return _data?.map((item, index) => {
      const { id, children, ...rest } = item
      return {
        id: `folder-${id}`,
        children: children?.map((i, j) => {
          /* eslint-disable */
          const { id, ...other } = i
          const temp = {
            id: `folder-${id}`,
            ...other,
          }
          return temp
        }),
        ...rest,
      }
    })
  }

  table = new TableStore({
    pagination: { pageSize: 20 },
    request: async (searchData) => {
      const { list } = await archivesManageApi.getList(searchData)
      const tempData = this.initLoop(list)
      const temp = this.loop(tempData)
      const changeKeys = this.changeKeys(temp)
      // console.log(temp, changeKeys)
      this.expandedRowKeys = [changeKeys[0]?.id]
      this.data = changeKeys
      return changeKeys
    },
  })
  /**
   * 申请下载Modal
   */
  createModal = new ModalStore()
  submit = async (data) => {
    const { rows } = this.table.getSelected()
    const arr = []
    rows?.map((item) => {
      const parentId = item?.parent[0]
      if (parentId && !arr.includes(parentId)) {
        arr.push(item?.parent[0])
      }
    })
    const reason = this.form.getFieldValue('reason')
    const params = {
      files: data,
      reason,
      archivesIds: arr,
    }
    await archivesManageApi.downloadEffect(params)
    message.success('申请下载成功！')
    this.createModal.close()
    this.table.search()
  }

  form = new FormStore()
  changeSelected = (e, selected) => {
    selected?.map((item) => {
      if (item.value === e) {
        this.form.setFieldsValue({
          clientName: item.clientName,
        })
      }
    })
  }

  /**
   * 发起归档Modal
   */
  fileModal = new ModalStore()
  submitFile = async (data) => {
    const { projName } = this.form.getFieldsValue()
    if (projName) {
      const params = {
        projId: data,
      }
      const { id } = await archivesManageApi.addFile(params)
      message.success('发起归档成功！')
      this.fileModal.close()
      history.push(`/archives/manage/detail/${id}`)
    } else {
      message.info('请将内容填写完整后发起归档！')
    }
  }

  //发起归档Modal搜索
  selectList = []
  getSelectList = async (val) => {
    const data = await archivesManageApi.search({
      page: 1,
      pageSize: 5,
      projVagueName: val,
      projReviewStatus: 'TAKE_EFFECT',
    })
    this.selectList = data
    return data
  }

  dataId = null
  onChangeSelect = (val) => {
    if (val) {
      const match = this.selectList.find((item) => item.id === val)
      this.form.setFieldsValue({
        clientName: match.clientNames || '',
      })
      this.dataId = val
    }
  }

  //列表文件下载
  downloadListFile = async (params) => {
    const res = await archivesManageApi.getFileDownload(params)
    if (res?.code === 200) {
      downFile(res)
      message.info('文件下载成功！')
    } else {
      msg && message.info(msg)
    }
  }
}
export default new Store()

import { makeAutoObservable } from '@zswl/admin'
import { ModalStore, TableStore } from '@zswl/components'
import { message } from 'antd'
import Api from '@/api/budget/lprApi'
import moment from 'moment'
import { downFile } from '@/utils'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  list = new TableStore({
    request: async (params) => {
      return await Api.getLPRList(params)
    },
  })
  editModal = new ModalStore({
    onFinish: async (values, initValues = {}) => {
      const { code, msg } = await Api.postLPRModify({
        ...values,
        lprDate: moment(values.lprDate).format('yyyy-MM-DD'),
        id: initValues.id,
      })
      if (code === 200) {
        message.info('保存成功！')
        this.list.search()
      } else {
        msg && message.info(msg)
      }
      this.editModal.close()
    },
  })
  itemEdit = (val) => {
    this.editModal.open({ ...val, lprDate: moment(val.lprDate) })
  }
  itemDelete = async (val) => {
    const { code, msg } = await Api.postLPRDelete({ id: val.id })
    if (code === 200) {
      message.info('删除成功！')
      this.list.search()
    } else {
      msg && message.info(msg)
    }
  }
  download = async (filename) => {
    const res = await Api.postLPRTemplateDownload({ filename })
    downFile(res)
    // const { code, msg } = res
    // if (code === 200) {
    //   message.info('下载成功')
    // } else {
    //   msg && message.info(msg)
    // }
  }
  upload = async (params, callback) => {
    const { code, msg } = await Api.postLPRImport(params)
    if (code === 200) {
      message.success('导入成功')
      this.list.search()
      callback && callback()
    } else {
      message.info(msg)
    }
  }
}
export default new Store()

import { PageStore, TableStore, FormStore } from '@zswl/components'
import { message } from 'antd'

import { makeAutoObservable, history } from '@zswl/admin'
import Api from '@/api/common/fileList'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  form = new FormStore()

  async submit(id) {
    const {
      name,
      templateId,
      fileName,
      fileType,
      filePath,
      createTime,
      createBy,
      targetDatabaseId,
      targetDatabase,
      targetTable,
      writeType,
    } = await this.form.submit()
    const params = {
      id,
      templateId: templateId,
      name,
      fileInfo: {
        fileName,
        fileType,
        filePath,
        createTime,
        createBy,
      },
      fileTaskModel: {
        targetDatabaseId,
        targetDatabase,
        targetTable,
        writeType,
        fieldList: this.detailTbale.getEditorData()?.list,
      },
    }
    if (id) {
      await http.post('/file/task/modify', params)
    } else {
      await http.post('/file/task/add', params)
    }
    message.success('保存成功')
    history.goBack()
  }
}
export default new Store()

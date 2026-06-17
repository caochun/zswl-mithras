import { FileTable } from '@/components'
import { observer } from '@zswl/admin'
import { Modal } from '@zswl/components'
import { message } from 'antd'
import { forwardRef, useImperativeHandle, useRef } from 'react'
import Api from './api'

const enumType = [
  {
    label: '起租材料',
    value: 'START_RENT',
  },
]

const Report = forwardRef(({ contractId, canEdit = true, businessVersion, title }, ref) => {
  const fileRef = useRef()
  const columns = [
    {
      title: '资料名称',
      dataIndex: 'name',
    },
  ]

  const params = {
    mainId: contractId,
    moduleType: 'CONTRACT',
    ext: {
      queryType: 'START_RENT',
    },
    businessVersion,
  }

  const autoGenerate = async () => {
    await Api.generateActualRentFile({
      contractId,
    })
    message.success('操作成功')
    fileRef.current.table.search()
  }

  useImperativeHandle(ref, () => ({
    autoGenerate,
  }))

  const rentFileRegex = /^实际租金表(\(\d+\))?\.docx$/

  const beforeUpload = (file) => {
    if (rentFileRegex.test(file.name)) {
      message.error("'实际租金表.docx'文件不允许手工上传")
      return Promise.reject()
    }
    return Promise.resolve()
  }

  const beforeDelete = (record, table) => {
    const fileName = record.fileName ?? record.filename ?? record?.name
    if (rentFileRegex.test(fileName?.value) || rentFileRegex.test(fileName)) {
      const list = table.getList() || []
      const allFiles = list.flatMap((item) => item.children || [])
      const rentFileCount = allFiles.filter((f) => {
        const name = f.fileName ?? f.filename ?? f.name
        return rentFileRegex.test(name?.value) || rentFileRegex.test(name)
      }).length
      if (rentFileCount === 1) {
        Modal.warning({
          title: '该文件不可删除！',
        })
        return false
      }
    }
    return true
  }

  return (
    <FileTable
      ref={fileRef}
      enumType={enumType}
      uploadApi={async ({ file, fileType: contractType }) => {
        return Api.upload({ fileArray: file, contractType, contractId })
      }}
      title={title}
      canEdit={canEdit}
      hideRowEdit={true}
      columns={columns}
      params={params}
      functionCodeList={{
        remove: 'contractStartRentFileBatchRemove',
        download: 'contractStartRentFileDownload',
        batchDownload: 'contractStartRentFileBatchDownload',
      }}
      uploadProps={{ beforeUpload }}
      beforeDelete={beforeDelete}
    />
  )
})
export default observer(Report)

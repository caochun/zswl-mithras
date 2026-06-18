import { fileApi as fileListApi } from '@/utils/api/tableFileApi'
import { downFile, downUrl, toHump } from '@/utils'
import { Modal } from '@zswl/components'
import { message } from 'antd'
import _ from 'lodash'

const getFileExtension = (filename) => {
  const name = filename?.value || filename
  return name?.split('.').pop()
}
const useFile = ({
  functionCodeList,
  canDownloadAll,
  params,
  afterDelete,
  beforeDelete,
  table,
  uploadApi,
  removeApi,
  downloadApi,
  batchDownloadApi,
  isFormApproval,
  needBusinessVersion,
}) => {
  const getId = (id) => {
    return id?.value ?? id
  }

  const deleteFile = async (id, record) => {
    let name = isFormApproval
      ? record.fileName?.value ?? record.filename?.value ?? record?.name?.value
      : record.fileName ?? record.filename ?? record?.name
    if (!name) {
      name = record.fileName ?? record.filename ?? record?.name
    }
    const newId = getId(id)

    if (_.isFunction(beforeDelete)) {
      const canDelete = await beforeDelete(record, table)
      if (!canDelete) return
    }

    Modal.confirm({
      title: `您将删除文件 '${name}'，请确认！`,
      onOk: async () => {
        const functionCode =
          functionCodeList?.remove || `${toHump(params.moduleType)}FileBatchRemove`
        let res
        if (_.isFunction(removeApi)) {
          res = await removeApi({ id: newId }, record)
        } else {
          res = await fileListApi.postBatchRemove({ ...params, fileIds: [newId] }, functionCode)
        }
        const { code, msg } = res
        if (code === 200) {
          message.success('删除成功')
          afterDelete?.(record)
          table?.search()
        } else {
          message.info(msg)
        }
      },
    })
  }
  const onBatchDownload = async () => {
    const { keys, rows } = table.getSelected()
    const ids = keys.filter((item) => item?.toString().indexOf('folder') === -1)
    const listRows = rows.filter((item) => item?.id?.toString().indexOf('folder') === -1)

    if (ids.length === 0 && !canDownloadAll) {
      message.info('请先勾选需要下载的文件')
      return
    }
    const fileId = ids.map((item) => item?.value ?? item)
    if (_.isFunction(batchDownloadApi)) {
      return batchDownloadApi({
        fileIds: fileId,
      })
    }
    const { ext: _ext, businessVersion, ...rest } = params
    const newBusinessVersion = needBusinessVersion({}) ? businessVersion : undefined
    const url = downUrl('/file/batch/download', {
      ...rest,
      businessVersion: newBusinessVersion,
      fileId,
      idType: listRows[0]?.idType,
    })
    window.open(url)
  }
  const preview = async ({ record, id, editType = 1, idType, fileName }) => {
    const businessVersion = params?.businessVersion
    const newBusinessVersion = needBusinessVersion(record) ? businessVersion : undefined

    const versionQuery = `${newBusinessVersion ? `&businessVersion=${newBusinessVersion}` : ''}`
    const newId = getId(id)
    const idTypeQuery = `${idType ? `&idType=${idType}` : ''}`
    if (['pdf'].includes(getFileExtension(fileName))) {
      window.open(`/preview/pdfPreview/${newId}?editType=${editType}${versionQuery}${idTypeQuery}`)
    } else {
      window.open(
        `/preview/reportPreview/${newId}?editType=${editType}${versionQuery}${idTypeQuery}`
      )
    }
  }
  const upload = async (data) => {
    if (_.isFunction(uploadApi)) {
      return await uploadApi(data)
    } else {
      const functionCode = functionCodeList?.upload || `${toHump(params.moduleType)}FileUpload`
      const { ext: _ext, ...rest } = params

      return await fileListApi.postFileUpload(
        { materialsType: 'DEFAULT', ...data, ...rest },
        functionCode
      )
    }
  }
  const download = async (id, record) => {
    const newId = getId(id)
    if (_.isFunction(downloadApi)) {
      return downloadApi({ id: newId }, record)
    }
    const functionCode = functionCodeList?.download || `${toHump(params.moduleType)}FileDownload`
    const businessVersion = params?.businessVersion
    const newBusinessVersion = needBusinessVersion(record) ? businessVersion : undefined
    const res = await fileListApi.getFileDownload(
      { ...params, businessVersion: newBusinessVersion, fileId: newId, idType: record.idType },
      functionCode
    )
    downFile(res)
  }
  return { deleteFile, onBatchDownload, preview, upload, download }
}
export default useFile

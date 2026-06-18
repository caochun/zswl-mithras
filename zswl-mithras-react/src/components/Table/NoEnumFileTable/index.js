import { forwardRef, useImperativeHandle, useEffect, useMemo, useState, useRef } from 'react'
import { Space, message, Modal, Tooltip } from 'antd'
import { Button, Table, TableStore } from '@zswl/components'
import { observer, getQuery } from '@zswl/admin'
import styles from './index.less'
import IconFont from '@/components/Icon'
import { fileApi as commonApi, fileCompareApi as compareApi } from '@/utils/api/tableFileApi'
import _, { flatMap } from 'lodash'
import { ImportAction } from '@/components/Actions'
import { downFile, downUrl, toHump } from '@/utils'
import { FiledFormat } from '@/components/Format'
import { saveServer } from '@/utils'

const OFFICE_SUFFIX = ['doc', 'docx', 'xls', 'xlsx', 'ppt', 'txt', 'wps']

const Index = (props, ref) => {
  const {
    tableApi,
    uploadApi,
    downloadApi,
    removeApi,
    batchDownloadApi,
    functionCodeList = {},
    params,
    onBeforeUpload
  } = props
  const {
    columns = [],
    title,
    extra = [],
    canEdit,
    canEditItem,
    canDelete,
    canBatchDownload = true,
    canDownload = true,
    canUpload = false,
    needApproval = true,
    //没有批量下载时也要多选
    enableSelect = false,
    hideOperation
  } = props
  // useEffect(() => {
  //   params && table.search()
  // }, [JSON.stringify(params)])

  const getValue = (val) => {
    return val?.value ?? val
  }
  const getFileName = (record) => {
    return getValue(record.name) ?? getValue(record.filename) ?? getValue(record.fileName)
  }

  const deleteFile = async (record) => {
    const name = getFileName(record)
    const fileId = getValue(record.id)
    Modal.confirm({
      title: `您将删除文件 '${name}'，请确认！`,
      onOk: async () => {
        const functionCode =
          functionCodeList?.remove || `${toHump(params.moduleType)}FileBatchRemove`
        let res
        if (_.isFunction(removeApi)) {
          res = await removeApi({ id }, record)
        } else {
          res = await commonApi.postBatchRemove({ ...params, fileIds: [fileId] }, functionCode)
        }
        const { code, msg } = res
        if (code === 200) {
          message.success('删除成功')
          table?.search()
        } else {
          message.info(msg)
        }
      },
    })
  }

  const download = async (record) => {
    const fileId = getValue(record.id)
    const idType = getValue(record.idType)
    if (_.isFunction(downloadApi)) {
      return downloadApi({ id: fileId }, record)
    }
    const functionCode = functionCodeList?.download || `${toHump(params.moduleType)}FileDownload`
    const res = await commonApi.getFileDownload(
      { materialsType: 'DEFAULT', ...params, idType, fileId },
      functionCode
    )
    downFile(res)
  }
  const upload = async (data) => {
    if (_.isFunction(onBeforeUpload)) {
      const result = await onBeforeUpload(data)
      if (result === false) {
        return
      }
    }
    if (!params.mainId) {
      console.log('params: ', params)
      message.info('请先保存表单')
      return
    }
    if (_.isFunction(uploadApi)) {
      const res = await uploadApi(data)
      table.search()
      return res
    }
    const functionCode = functionCodeList?.upload || `${toHump(params.moduleType)}FileUpload`
    const { version, businessVersion, ...restParams } = params
    const res = await commonApi.postFileUpload(
      { materialsType: 'DEFAULT', ...data, ...restParams },
      functionCode
    )
    table.search()
    return res
  }

  const getFileExtension = (filename) => {
    const name = getValue(filename)
    return name?.split('.').pop()
  }

  const canEditFormFileExtension = (filename) => {
    const suffix = getFileExtension(filename)
    return OFFICE_SUFFIX.includes(suffix)
  }

  const preview = async ({ id, editType = 1, idType, fileName }) => {
    const businessVersion = params?.businessVersion
    const versionQuery = `${businessVersion ? `&businessVersion=${businessVersion}` : ''}`
    const idTypeQuery = `${idType ? `&idType=${idType}` : ''}`
    if (['pdf'].includes(getFileExtension(fileName))) {
      window.open(`/preview/pdfPreview/${id}?editType=${editType}${versionQuery}${idTypeQuery}`)
    } else {
      window.open(`/preview/reportPreview/${id}?editType=${editType}${versionQuery}${idTypeQuery}`)
    }
  }

  const filedRender = (val, record, column) => {
    const value = val?.value !== undefined ? val?.value : val
    // 组件使用的时候,dataIndex包含以下
    if (['fileName', 'filename', 'name'].includes(column.dataIndex)) {
      return (
        <Tooltip title={value} placement="top">
          <div className="z-single-line" style={{ color: '#2552e6', cursor: 'pointer' }}>
            <span
              onClick={() =>
                preview({
                  id: record.id,
                  idType: record.idType,
                  fileName: getFileName(record),
                })
              }
            >
              {value}
            </span>
          </div>
        </Tooltip>
      )
    }

    return <FiledFormat title={value} isChange={val?.isChange} needBar={false} />
  }

  const getCanEdit = (record) => {
    if (_.isFunction(canEditItem)) {
      return canEditItem(record)
    } else if (_.isBoolean(canEditItem)) {
      return canEditItem
    } else {
      return canEdit
    }
  }

  const getCanDelete = (record) => {
    if (_.isFunction(canDelete)) {
      return canDelete(record)
    } else if (_.isBoolean(canDelete)) {
      return canDelete
    } else {
      return canEdit
    }
  }

  const getDownload = (record) => {
    if (_.isFunction(canDownload)) {
      return rename(record)
    } else if (_.isBoolean(canDownload)) {
      return canDownload
    }
  }
  const items = useMemo(() => {
    return [
      ...columns.map((v) => {
        return {
          render: (value, record) => filedRender(value, record, v),
          ...v,
        }
      }),
      !hideOperation && {
        title: '操作',
        fixed: 'right',
        width: 150,
        actions: (record) => {
          return [
            getCanEdit(record) &&
              canEditFormFileExtension(getFileName(record)) && {
                name: '编辑',
                onClick: () =>
                  preview({
                    id: record.id,
                    editType: 2,
                    idType: record.idType,
                    fileName: getFileName(record),
                  }),
              },
            getDownload(record) && {
              name: '下载',
              onClick: () => download(record),
            },
            getCanDelete(record) && {
              name: '删除',
              onClick: () => deleteFile(record),
            },
          ]
        },
      },
    ]
  }, [columns])
  const isFormApproval = getQuery('typeId') == 'approval' && getQuery('tab') !== 'revocation'
  const getList = async () => {
    if (_.isFunction(tableApi)) return await tableApi()
    if (isFormApproval && needApproval) {
      const data = await compareApi.postFileListCompare(params)
      return data
    }
    const funcCode = functionCodeList?.fileList
    return await getFileList(params, { funcCode })
  }
  const table = useMemo(
    () =>
      new TableStore({
        pagination: false,
        request: async () => {
          const res = await getList({ ...params })
          const list = res?.list ?? res
          const data = list?.map(({ id, filename, ...item }, index) => {
            return {
              id: id?.value ?? id,
              key: id,
              name: filename,
              filename,
              ...item,
            }
          })
          return data
        },
      }),
    [JSON.stringify(params)]
  )

  useImperativeHandle(ref, () => ({
    table,
    filedRender,
  }), [])

  const batchDownload = async (ids) => {
    if (_.isFunction(batchDownloadApi)) return await batchDownloadApi(ids)
    await batchDownloadFile({ ...params, fileId: ids })
  }
  const onBatchDownload = async () => {
    const { keys: ids } = table.getSelected()
    if (!ids.length) {
      message.info('请选择需要下载的文件')
      return
    }
    await batchDownload(ids)
    message.success('下载成功')
  }
  const beforeUpload = () => {
    table?.search()
  }

  const hasFilesList = table.getList()?.length > 0
  return (
    <div className={styles.page}>
      <div className={styles.js}>
        <div className={styles.title}>{title}</div>
        <Space key="edit">
          {extra}
          {(canEdit || canUpload) && (
            <ImportAction upload={upload} beforeUpload={beforeUpload} mode="upload" accept="*" />
          )}
          {canBatchDownload && hasFilesList && (
            <Button onClick={onBatchDownload} key="batchDownload">
              <IconFont type="icon-icon_download" />
              批量下载
            </Button>
          )}
        </Space>
      </div>
      <Table
        resizable
        columnsFilter={'Table_NoEnumFileTable_1'}
        onFilter={(key, val) => saveServer('Table_NoEnumFileTable_1', val)}
        selectable={canBatchDownload || enableSelect}
        store={table}
        columns={items}
        // scroll={{ x: 1000 }}
        columnWidth={180}
        scroll={{ x: 'auto' }}
      />
    </div>
  )
}
export const batchDownloadFile = async (params) => {
  const { ext, ...rest } = params
  const url = downUrl('/file/batch/download', {
    ...rest,
    colZipFlag: true,
  })
  window.open(url)
}

const getFileList = async (params, otherParams = {}) => {
  const functionCode = otherParams?.funcCode || `${toHump(params.moduleType)}FileList`
  const { version, businessVersion, ...restParams } = params
  if (!params.mainId) return []
  return await commonApi.postFileList(
    {
      pageSize: 1000,
      ...restParams,
    },
    functionCode
  )
}
export const noEnumDownloadAll = async (params) => {
  const res = await getFileList(params)
  if (!res?.list?.length) {
    message.info('没有文件可以下载')
    return Promise.reject()
  }
  return await batchDownloadFile({ ...params, fileId: res?.list?.map(({ id }) => id) })
}
export default observer(forwardRef(Index))

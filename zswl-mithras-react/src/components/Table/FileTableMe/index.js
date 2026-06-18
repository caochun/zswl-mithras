import { fileApi as fileListApi, fileCompareApi as compareApi } from '@/utils/api/tableFileApi'
import { FiledFormat } from '@/components/Format'
import IconFont from '@/components/Icon'
import UploadModal from './Upload'
import { saveServer, toHump } from '@/utils'
import { getQuery, observer } from '@zswl/admin'
import { App, Button, ModalStore, Table } from '@zswl/components'
import { Checkbox, Space, Tag, Tooltip } from 'antd'
import _ from 'lodash'
import { forwardRef, useEffect, useImperativeHandle, useMemo, useRef, useState } from 'react'
import useFile from './fileHooks'
import styles from './index.less'
// 在文件顶部定义常量
const OFFICE_SUFFIX = ['doc', 'docx', 'xls', 'xlsx', 'ppt', 'txt', 'wps']
// 提取工具函数
const getFileExtension = (filename) => {
  const name = filename?.value || filename
  return name?.split('.').pop()
}
const canEditFormFileExtension = (filename) => {
  const suffix = getFileExtension(filename)
  return OFFICE_SUFFIX.includes(suffix)
}
const getId = (id) => {
  return id?.value ?? id
}
const Index = (props, ref) => {
  const {
    tableApi,
    uploadApi,
    removeApi,
    downloadApi,
    functionCodeList,
    params,
    canBatchDownload = true,
    hasFormApproval = true,
    columns,
    enumType,
    handleEnumType,
    title,
    canEdit,
    canDelete,
    canEditItem,
    listFormat,
    actions = [],
    extra = [],
    uploadTips,
    afterDelete,
    showChangeType = false,
    modalProps = {},
    uploadType = 'modalUpload',
    needBusinessVersion = () => true,
    //没有批量下载时也要多选
    enableSelect = false,
    //默认不需要展开所有时传入
    foldKeys = false,
    canDownload,
    disableFolderAction,
    //不使用组件内的tableApi时刷新数据
    refreshApi,
    dataSource,
    ...other
  } = props
  const [_data, _setData] = useState([])
  const [expandKeys, setExpandKeys] = useState([])
  const [isExpandStatus, setExpendStatus] = useState(true)
  const [selectedRowKeys, setSelectedRowKeys] = useState([])
  const showRemoveFilesRef = useRef(false)
  const { optionsType } = App.getData()
  const isFormApproval = hasFormApproval && getQuery('typeId') === 'approval'

  const [currentEnum, setCurrentEnum] = useState(undefined)
  const enumList = useMemo(() => {
    let enumTypeTemp = []
    if (_.isArray(enumType)) {
      enumTypeTemp = enumType
    } else {
      enumTypeTemp = optionsType[enumType]
    }
    if (uploadType === 'rowUpload') {
      enumTypeTemp = enumTypeTemp?.filter((item) => item.value === currentEnum)
      return enumTypeTemp
    }

    if (_.isFunction(handleEnumType)) enumTypeTemp = handleEnumType(enumTypeTemp)
    return enumTypeTemp
  }, [enumType, optionsType, handleEnumType, currentEnum])

  const fileListFormat = (list) => {
    return (list || []).map((item, index) => {
      return {
        id: `folder-${index}`,
        key: `folder-${index}`,
        enumType: item.enumType,
        name: App.matchOption(enumType, item.key).label || item.groupTypeName || item.key,
        children: item.value.map(({ fileName, filename, id, ...itemData }, j) => {
          return {
            name: fileName ?? filename,
            fileName: fileName ?? filename,
            id: getId(id),
            ...itemData,
          }
        }),
      }
    })
  }
  const table = Table.useStore(
    {
      pagination: false,
      request: async () => {
        const res = await getList()
        const data = listFormat?.(res) ?? fileListFormat(res)
        const files = filterRemoveFiles(data)

        // 默认展开所有
        !foldKeys && setExpandKeys(files?.map(({ id }, index) => id))
        return files
      },
    },
    []
  )
  useEffect(() => {
    const data = listFormat?.(dataSource) ?? fileListFormat(dataSource)
    const files = filterRemoveFiles(data)
    !foldKeys && setExpandKeys(files?.map(({ id }, index) => id))
    _setData(files)
  }, [JSON.stringify(dataSource)])
  // 过滤已经“删除”的文件
  const filterRemoveFiles = (data) => {
    if (showRemoveFilesRef.current) {
      return data
    }
    const newData = []
    data?.map(({ children, ...rest }) => {
      const obj = { ...rest, children: [] }
      children?.map((i) => {
        const name = i.filename || i.fileName || i.name
        if (name?.changeType !== 'REMOVE') {
          obj.children.push(i)
        }
      })
      newData.push(obj)
    })
    return newData
  }
  const { deleteFile, onBatchDownload, preview, upload, download } = useFile({
    functionCodeList,
    params,
    afterDelete,
    table,
    uploadApi,
    removeApi,
    downloadApi,
    isFormApproval,
    needBusinessVersion,
    refreshApi,
  })

  const filedRender = (val, record, column) => {
    const value = val?.value !== undefined ? val?.value : val
    // 组件使用的时候,dataIndex包含以下
    if (['fileName', 'filename', 'name'].includes(column.dataIndex)) {
      if (record.id?.toString().indexOf('folder') > -1) {
        return value
      }
      return (
        <Tooltip title={value} placement="top">
          <a
            onClick={() =>
              preview({
                record,
                id: record.id,
                idType: record.idType,
                fileName: record.name,
              })
            }
          >
            {value}
          </a>
        </Tooltip>
      )
    }
    return <FiledFormat title={value} isChange={val?.isChange} needBar={false} />
  }
  const ChangeDom = ({ changeType }) => {
    const obj = {
      ADD: <Tag color="#108ee9">新增</Tag>,
      REMOVE: <Tag color="red">删除</Tag>,
    }
    if (!changeType) return '-'
    return <span style={{ color: 'red' }}>{obj[changeType]}</span>
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
    }
  }
  const getDownload = (record) => {
    if (_.isFunction(canDownload)) {
      return canDownload(record)
    } else if (_.isBoolean(canDownload)) {
      return canDownload
    }
  }
  const getDisableFolder = (record) => {
    if (_.isFunction(disableFolderAction)) {
      return disableFolderAction(record)
    } else if (_.isBoolean(disableFolderAction)) {
      return disableFolderAction
    }
  }
  const items = useMemo(() => {
    return [
      isFormApproval &&
        showChangeType && {
          title: '变更类型',
          dataIndex: 'name',
          width: 110,
          render(val, record) {
            if (record.id.toString().indexOf('folder') > -1) {
              return null
            }
            const changeType = val?.changeType || record.changeType
            return <ChangeDom changeType={changeType}> </ChangeDom>
          },
        },
      ...columns.map((v) => {
        return {
          render: (value, record) => filedRender(value, record, v),
          ...v,
        }
      }),
      {
        title: '操作',
        fixed: 'right',
        width: 220,
        render: (text, record) => {
          const isFolder = record?.id.toString().indexOf('folder') > -1
          if (isFolder) {
            const canUpload = uploadType === 'rowUpload' && canEdit
            if (!canUpload || getDisableFolder(record)) return null
            return (
              <>
                <Button.Upload
                  key="upload"
                  size="small"
                  onClick={() => {
                    setCurrentEnum(record.enumType)
                    setTimeout(() => {
                      modal.open({ enumType: record.enumType })
                    }, 0)
                  }}
                >
                  上传
                </Button.Upload>
              </>
            )
          }
          const changeType = record.changeType
          if (changeType === 'REMOVE') {
            return null
          }
          return (
            <div style={{ display: 'flex' }}>
              {getCanEdit(record) && canEditFormFileExtension(record.name) && (
                <Button
                  key="edit"
                  type="text"
                  style={{ color: '#2552e6', paddingLeft: 0, paddingRight: 3 }}
                  onClick={() =>
                    preview({
                      record,
                      id: record.id,
                      editType: 2,
                      idType: record.idType,
                      fileName: record.name,
                    })
                  }
                >
                  编辑
                </Button>
              )}
              {getDownload(record) && (
                <Button
                  type="text"
                  style={{ color: '#2552e6', paddingLeft: 0, paddingRight: 3 }}
                  key="download"
                  onClick={() => download(record.id, record)}
                >
                  下载
                </Button>
              )}
              {getCanDelete(record) && (
                <Button
                  type="text"
                  key="delete"
                  style={{ color: '#2552e6', paddingLeft: 0, paddingRight: 3 }}
                  onClick={() => deleteFile(record.id, record)}
                >
                  删除
                </Button>
              )}
            </div>
          )
        },
      },
    ].filter(Boolean)
  }, [columns, canDelete, canEdit, isFormApproval, showChangeType, uploadType])
  const getList = async () => {
    if (_.isFunction(tableApi)) {
      return await tableApi()
    }
    if (isFormApproval) {
      const data = await compareApi.postListGroupCompare(params)
      return data
    }
    const functionCode = functionCodeList?.fileList || `${toHump(params.moduleType)}FileListGroup`
    const { version, businessVersion, ...restParams } = params
    return fileListApi.postListGroup({ ...restParams, businessVersion }, functionCode)
  }
  useEffect(() => {
    params && table.search()
  }, [JSON.stringify(params)])
  const modal = useMemo(() => new ModalStore({}), [])
  useImperativeHandle(ref, () => ({
    table,
    filedRender,
    modal,
    setExpandKeys,
    enumList,
    selectedRowKeys,
    setSelectedRowKeys,
  }))
  const onShowRemoveBtnChange = (e) => {
    showRemoveFilesRef.current = e.target.checked
    table.search()
    setExpendStatus(true)
  }
  const onExpandFold = () => {
    const fileList = table.getList()
    if (isExpandStatus) {
      setExpandKeys([])
    } else {
      setExpandKeys(fileList?.map((item, index) => `folder-${index}`))
    }
    setExpendStatus(!isExpandStatus)
  }
  useEffect(() => {
    if (expandKeys.length > 0) {
      setExpendStatus(true)
    } else {
      setExpendStatus(false)
    }
  }, [expandKeys])
  const resRowSelection = useMemo(() => {
    if (!(canBatchDownload || enableSelect)) {
      return undefined
    }
    return {
      selectedRowKeys,
      onChange: (selectedKeys) => {
        const filteredKeys = selectedKeys.filter((key) => typeof key === 'number')
        setSelectedRowKeys(filteredKeys)
      },
      checkStrictly: false,
      getCheckboxProps: (record) => {
        const isDisabled = record.name?.changeType === 'REMOVE'
        return {
          disabled: isDisabled,
        }
      },
    }
  }, [canBatchDownload, enableSelect, selectedRowKeys])
  const hasFilesList = table.getList()?.length > 0
  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.title}>{title}</div>
        <Space key="edit">
          {isFormApproval && showChangeType && hasFilesList && (
            <Checkbox onChange={onShowRemoveBtnChange} checked={showRemoveFilesRef.current}>
              展示已删除的文件
            </Checkbox>
          )}

          {hasFilesList && (
            <Button onClick={onExpandFold}>{isExpandStatus ? '全部收起' : '全部展开'}</Button>
          )}

          {actions}
          {uploadType === 'modalUpload' && canEdit && (
            <Button key="upload" onClick={() => modal.open()}>
              <IconFont type="icon-icon_upload" />
              上传
            </Button>
          )}
          {canBatchDownload && hasFilesList && (
            <Button onClick={onBatchDownload} key="batchDownload" type="primary">
              <IconFont type="icon-icon_download" />
              批量下载
            </Button>
          )}
          {extra}
        </Space>
      </div>
      <Table
        columnWidth={180}
        resizable
        columnsFilter={'Table_FileTable_1'}
        onFilter={(key, val) => saveServer('Table_FileTable_1', val)}
        //store={table}
        rowSelection={resRowSelection}
        dataSource={_data}
        autoRequest={false}
        expandable={{
          expandedRowKeys: expandKeys,
          onExpand: (expanded, record) => {
            if (expanded) {
              setExpandKeys([...expandKeys, getId(record.id)])
            } else {
              setExpandKeys(expandKeys.filter((item) => item !== record.id))
            }
          },
        }}
        columns={items}
        {...other}
      />
      <UploadModal
        modalStore={modal}
        tableStore={table}
        refreshApi={refreshApi}
        uploadApi={upload}
        enumList={enumList}
        title={title}
        uploadTips={uploadTips}
        {...modalProps}
      />
    </div>
  )
}
export default observer(forwardRef(Index))

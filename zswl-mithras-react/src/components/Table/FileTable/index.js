import compareApi from '@/api/common/editableCompare'
import fileListApi from '@/api/common/fileList'
import { FiledFormat } from '@/components/Format'
import IconFont from '@/components/Icon'
import UploadModal from './Upload'
import { saveServer, toHump } from '@/utils'
import { getQuery, observer } from '@zswl/admin'
import { App, Button, Form, ModalStore, Table } from '@zswl/components'
import { Checkbox, Col, Input, Row, Select, Space, Tag, Tooltip, message } from 'antd'
import _ from 'lodash'
import { forwardRef, useEffect, useImperativeHandle, useMemo, useRef, useState } from 'react'
import useFile from './fileHooks'
import styles from './index.less'
import RenameModal from './renameModal'
import { canEditFormFileExtension, getCanDelete, getCanEdit, getDisableFolder, getDownload, getId, getRename, parts } from './utils'

const Index = (props, ref) => {
  const {
    tableApi,
    uploadApi,
    removeApi,
    downloadApi,
    batchDownloadApi,
    functionCodeList,
    params,
    canBatchDownload = true,
    hasFormApproval = true,
    columns,
    enumType,
    enumList: enumListFromProps, // 支持从父组件传入 enumList
    handleEnumType,
    title,
    canEdit,
    rename,
    canDelete,
    canEditItem,
    listFormat,
    actions = [],
    extra = [],
    uploadTips,
    afterDelete,
    beforeDelete,
    showChangeType = false,
    modalProps = {},
    uploadType = 'modalUpload',
    uploadProps = {},
    needBusinessVersion = () => true,
    //没有批量下载时也要多选
    enableSelect = false,
    //默认不需要展开所有时传入
    foldKeys = false,
    canDownload = true,
    canDownloadAll = false,
    disableFolderAction,
    hideRowEdit = false,
    hideOperation,
    isRiskManagerProj,
    commentGuideLine,
    itemProps,
    ...other
  } = props
  const [comment, setComment] = useState(isRiskManagerProj ? itemProps.reviewComments : '')
  const [instruction, setInstruction] = useState(isRiskManagerProj ? itemProps.reviewInstructions : '')
  const [expandKeys, setExpandKeys] = useState([])
  const [RenameModalRecord, setRenameModalRecord] = useState(null)
  const [isExpandStatus, setExpendStatus] = useState(true)
  const showRemoveFilesRef = useRef(false)
  const { optionsType } = App.getData()
  const isFormApproval = hasFormApproval && getQuery('typeId') === 'approval' && getQuery('tab') !== 'revocation'
  const [currentEnum, setCurrentEnum] = useState(undefined)
  const enumList = useMemo(() => {
    if (enumListFromProps) {
      return enumListFromProps
    }

    let enumTypeTemp = []
    if (_.isArray(enumType)) {
      enumTypeTemp = enumType
    } else {
      enumTypeTemp = optionsType[enumType]
    }
    if (uploadType === 'rowUpload') {
      const childrenEnum = enumTypeTemp?.find((item) => item.value === currentEnum)?.childSelectName
      if (!childrenEnum) {
        enumTypeTemp = enumTypeTemp?.filter((item) => item.value === currentEnum)
        return enumTypeTemp
      } else {
        enumTypeTemp = optionsType[childrenEnum] ?? []
      }
    }

    if (_.isFunction(handleEnumType)) enumTypeTemp = handleEnumType(enumTypeTemp)
    return enumTypeTemp
  }, [enumListFromProps, enumType, optionsType, handleEnumType, currentEnum])
  const renameFileModalStore = useMemo(() => new ModalStore({}), [])

  const [form] = Form.useForm()
  const fileListFormat = (list) => {
    return (list || []).map((item, index) => {
      return {
        id: `folder-${index}`,
        key: `folder-${index}`,
        enumType: item.enumType ?? item.key,
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

        // 默认展开逻辑
        if (!foldKeys) {
          // 默认展开所有
          setExpandKeys(files?.map(({ id }) => id))
        } else if (typeof foldKeys === 'function') {
          // 支持函数形式，只展开符合条件的行
          setExpandKeys(files?.filter(foldKeys).map(({ id }) => id))
        }
        return files
      },
    },
    []
  )
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
  })
  const renameFile = async (record) => {
    form.setFieldsValue({
      filename: parts(record),
      suffix: record?.suffix,
    })
    setRenameModalRecord(record)
    renameFileModalStore.open(record)
  }
  const handleOk = async () => {
    const data = form.getFieldValue()
    if (!data.filename) {
      return
    }
    const newId = RenameModalRecord.id
    const functionCode = functionCodeList?.remove || `${toHump(params.moduleType)}FileRename`
    const newFileName = `${data.filename}.${data.suffix?.value ? data.suffix?.value : data.suffix}`
    let res = await fileListApi.postRenameFile({ ...params, newFileName, fileId: newId }, functionCode)
    const { code, msg } = res
    if (code === 200) {
      message.success('重命名成功')
      table?.search()
    } else {
      message.info(msg)
    }
    renameFileModalStore.close()
  }
  const filedRender = (val, record, column) => {
    if (column.customCol && column.customCol.condition === val) {
      return column.customCol.cb(val, record, column)
    }
    const value = val?.value !== undefined ? val?.value : val
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
            style={{ color: record?.isChange ? 'red' : '#2552e6' }}
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
      !hideOperation && {
        title: '操作',
        fixed: 'right',
        width: 180,
        actions: (record) => {
          const isFolder = record?.id.toString().indexOf('folder') > -1
          if (isFolder) {
            const canUpload = uploadType === 'rowUpload' && canEdit
            if (!canUpload || getDisableFolder(disableFolderAction, record)) return null
            return [
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
              </Button.Upload>,
            ]
          }
          const changeType = record.changeType
          if (changeType === 'REMOVE') {
            return null
          }
          return [
            getCanEdit(canEditItem, canEdit, record) &&
              canEditFormFileExtension(record.name) &&
              !hideRowEdit && {
                name: '编辑',
                key: 'edit',
                onClick: () =>
                  preview({
                    record,
                    id: record.id,
                    editType: 2,
                    idType: record.idType,
                    fileName: record.name,
                  }),
              },
            getDownload(canDownload, record) && {
              name: '下载',
              key: 'download',
              onClick: () => {
                download(record.id, record)
              },
            },
            getCanDelete(canDelete, canEdit, record) && {
              name: '删除',
              key: 'delete',
              onClick: () => {
                deleteFile(record.id, record)
              },
            },
            getRename(rename, record) && {
              name: '重命名',
              onClick: () => renameFile(record),
            },
          ].filter(Boolean)
        },
      },
    ].filter(Boolean)
  }, [columns, canDelete, canEdit, isFormApproval, showChangeType, uploadType])
  const getList = async () => {
    if (_.isFunction(tableApi)) return await tableApi()
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
          {actions}
          {hasFilesList && <Button onClick={onExpandFold}>{isExpandStatus ? '全部收起' : '全部展开'}</Button>}
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
      <Row style={{ paddingLeft: 23, paddingBottom: 15, paddingTop: 15 }}>
        {isRiskManagerProj && (
          <Col span={5}>
            <span className={styles.labelrequire} style={{ display: 'inline-block', width: 67 }}>
              审核意见:
            </span>
            <Select
              style={{ width: 130 }}
              value={comment}
              onChange={(value) => {
                setComment(value)
                value !== 'SUPPLEMENT_INFO' && instruction && setInstruction('')
                commentGuideLine({
                  reviewComments: value,
                  reviewInstructions: instruction,
                })
              }}
              options={optionsType['projReviewMaterialCommentsEnum'] || []}
            />
          </Col>
        )}
        {isRiskManagerProj && comment === 'SUPPLEMENT_INFO' && (
          <Col span={18} style={{ display: 'flex' }}>
            <span style={{ width: 80 }}>审核说明:</span>
            <Input.TextArea
              value={instruction}
              autoSize={{
                minRows: 3,
              }}
              onChange={(v) => {
                setInstruction(v.target.value)
              }}
              onBlur={(e) =>
                commentGuideLine({
                  reviewInstructions: e.target.value,
                  reviewComments: comment,
                })
              }
            />
          </Col>
        )}
      </Row>
      <Table
        columnWidth={180}
        resizable
        columnsFilter={'Table_FileTable_1'}
        onFilter={(key, val) => saveServer('Table_FileTable_1', val)}
        selectable={
          (canBatchDownload || enableSelect) && {
            checkStrictly: false,
            getCheckboxProps: (record) => {
              return {
                disabled: record.name?.changeType === 'REMOVE',
              }
            },
          }
        }
        store={table}
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
        uploadApi={upload}
        enumList={enumList}
        title={title}
        uploadTips={uploadTips}
        uploadProps={uploadProps}
        {...modalProps}
      />
      <RenameModal handleOk={handleOk} record={RenameModalRecord} modalStore={renameFileModalStore} form={form} />
    </div>
  )
}
export default observer(forwardRef(Index))

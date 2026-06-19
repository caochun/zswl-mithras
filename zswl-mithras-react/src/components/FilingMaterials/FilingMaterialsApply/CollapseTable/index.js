import Collapse from '@/components/Collapse'
import { FileTableMe, NoEnumFileTable } from '@/components/Table'
import { getUserInfo } from '@/utils'
import { downFile } from '@/utils/downFunction'
import { getQuery, observer } from '@zswl/admin'
import { Button, Modal, Space, message } from 'antd'
import _ from 'lodash'
import moment from 'moment'
import { useEffect, useMemo, useRef, useState } from 'react'
import { useFillingMaterialContext } from '../Context'
import Api from '@/api/filingMaterials/filingMaterialsApplyApi'
import { tableEnum } from '../enum'

const Index = ({
  id,
  dataSource,
  collapseName,
  businessType,
  moduleCode,
  clientTypeName,
  canBatchDownload,
  custDirEnum,
  enableSelect,
  canEdit,
  name,
  basic,
  clientId,
}) => {
  const { activeTab, enumType, refreshTable, taskActivityId, curTaskActivityIds, startUserName, curTaskDefKey } = useFillingMaterialContext()
  const userName = getUserInfo().userName
  const columns = [
    { title: '资料清单', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName', width: 80 },
    { title: '上传时间', dataIndex: 'createTime', width: 160 },
  ]
  const revocation = getQuery('tab') == 'revocation'
  const sendBack = getQuery('tab') == 'sendback'
  const isPending = getQuery('curTab') == 'pending'
  const isProjectManager = startUserName === userName
  const canEditFlag =
    revocation ||
    sendBack ||
    (['userTask_initialReview', 'userTask_startUser', 'userTask_review', 'userTask_filingManager'].includes(curTaskDefKey) && isPending)
  const hideActions = !canEditFlag
  const isStartUser = taskActivityId === 'userTask_startUser' || curTaskActivityIds === 'userTask_startUser'
  const syncStatus = ['userTask_initialReview', 'userTask_review'].includes(curTaskDefKey)
  const [loading, setLoading] = useState(false)
  const tableRef = useRef(null)
  const [showSyncBtn, setShowSyncBtn] = useState(false)
  useEffect(() => {
    getSyncBtn({
      id,
      clientId,
      businessType,
    })
  }, [id, clientId])

  const getSyncBtn = async (params) => {
    const result = await Api.showSynchronizationButton(params)
    setShowSyncBtn(result)
  }

  const handleDownload = async (e) => {
    e.stopPropagation()
    setLoading(true)
    const fileIds = tableRef.current?.selectedRowKeys ?? []
    await Api.batchDownload({
      id,
      tabCode: activeTab,
      moduleCode,
      clientId,
      fileIds,
    })
    setLoading(false)
  }

  const handleSync = (e) => {
    e.stopPropagation()
    Modal.confirm({
      title: '资料同步',
      content: '是否将该客户在其他合同项下的归档资料同步到当前模块？',
      onOk: async () => {
        await Api.materialsSynchronization({
          id,
          clientId,
          businessType,
        })
        message.success('同步成功')
        refreshTable({ id, tabCode: activeTab, basic })
      },
    })
  }
  const extra = (
    <Space>
      <Button type="primary" size="small" onClick={handleDownload} loading={loading}>
        批量下载
      </Button>
      {showSyncBtn && syncStatus && (
        <Button type="primary" size="small" onClick={handleSync} disabled={hideActions}>
          资料同步
        </Button>
      )}
    </Space>
  )
  const params = {
    mainId: id,
    moduleType: businessType,
    sourceBusinessKey: clientId,
  }

  const formatData = (list, data) => {
    return list?.map((itm) => {
      const updateItem = data.find((d) => (basic ? d.materialName : d.key) === itm.value)
      return updateItem ? { ...updateItem } : itm
    })
  }
  const data = useMemo(() => {
    if (!Array.isArray(dataSource)) {
      return dataSource
    }
    const getCurData = () => {
      if (isStartUser) {
        return dataSource.filter((item) => (basic ? item.materialName : item.key) === 'BASIC_INFORMATION')
      } else {
        return formatData(enumType[custDirEnum], dataSource).filter(Boolean)
      }
    }
    return (
      getCurData()
        ?.map((item, index) => {
          if (basic) {
            return {
              id: `folder-${index}`,
              key: item.materialName || item.value,
              enumType: item.materialName || item.value,
              templateName: custDirEnum,
              value:
                item.materialList?.map((i) => {
                  return {
                    id: i.recordId,
                    name: { value: i.filename },
                    createByName: i.createByName,
                    enumType: item.materialName || item.value,
                    createTime: i.createTimestamp === -1 ? '' : moment(i.createTimestamp).format('YYYY-MM-DD HH:mm:ss'),
                    idType: i.idType,
                    businessType,
                  }
                }) || [],
            }
          } else {
            return {
              id: `folder-${index}`,
              key: item.key || item.value,
              enumType: item.key || item.value,
              templateName: tableEnum[activeTab],
              value: Array.isArray(item.value)
                ? item.value.map((i) => {
                    return {
                      id: i.id,
                      name: { value: i.filename },
                      createByName: i.createByName,
                      enumType: item.key || item.value,
                      createTime: i.createTime,
                      idType: i.idType,
                      businessType,
                    }
                  })
                : [],
            }
          }
        })
        ?.filter((item) => {
          if (isProjectManager) return item.key === 'BASIC_INFORMATION'
          if (item.key === 'BASIC_INFORMATION' || item.key === 'OTHER') return true
          return Array.isArray(item.value) && item.value.length > 0
        }) || []
    )
  }, [dataSource, basic, custDirEnum, isStartUser, isProjectManager])

  const canDelete = (record) => {
    return !hideActions
  }
  const canEditItem = (record) => {
    if (hideActions) {
      return false
    }
    return record.enumType === 'BASIC_INFORMATION'
  }
  const disableFolderAction = (record) => {
    return hideActions
  }
  const canDownloadTemplate = (record) => {
    return record.enumType === 'BASIC_INFORMATION'
  }
  const downloadApi = async ({ id }) => {
    const res = await Api.download({ fileId: id })
    downFile(res)
  }

  return (
    <Collapse
      header={<h4 style={{ marginBottom: 0 }}>{collapseName}</h4>}
      folded={!isStartUser}
      forceRender
      extra={extra}
      style={{ marginBottom: 16 }}
    >
      {Array.isArray(dataSource) ? (
        <FileTableMe
          dataSource={_.uniqBy(data, 'key')}
          enumType={basic ? enumType[custDirEnum] : enumType[tableEnum[activeTab]]}
          downloadApi={downloadApi}
          refreshApi={() => refreshTable({ id, tabCode: activeTab, basic })}
          params={params}
          ref={tableRef}
          pagination={false}
          canBatchDownload={canBatchDownload}
          enableSelect={enableSelect}
          uploadType="rowUpload"
          canDownload={true}
          canDownloadTemplate={canDownloadTemplate}
          removeApi={Api.materialsRemove}
          title={clientTypeName && <div className={'z-sub-title'}>{`${clientTypeName}(${name})`}</div>}
          tableApi={() => _.uniqBy(data, 'key')}
          canEdit={canEdit}
          canDelete={canDelete}
          disableFolderAction={disableFolderAction}
          columns={columns}
          foldKeys={!isStartUser}
          canEditItem={canEditItem}
        />
      ) : (
        <NoEnumFileTable
          tableApi={() => data}
          canBatchDownload={canBatchDownload}
          //title={item.groupName}
          canEdit={canEdit}
          canDownload={true}
          params={params}
          columns={columns}
        />
      )}
    </Collapse>
  )
}

export default observer(Index)

import Collapse from '@/components/Collapse'
import { FileTable, NoEnumFileTable } from '@/components/Table'
import { getUserInfo } from '@/utils'
import { getQuery, observer } from '@zswl/admin'
import { App } from '@zswl/components'
import { Button, Empty } from 'antd'
import moment from 'moment'
import { useRef, useState } from 'react'
import { useFillingMaterialContext } from '../Context'
import Api from '../api'
import { tableEnum } from '../enum'

const Index = ({ id, canEdit, dataSource, name, businessType, moduleCode, canBatchDownload, canDownload, enableSelect, basic, folded }) => {
  const columns = [
    { title: '资料清单', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]
  const { activeTab, enumType, startUserName, processStatus, curTaskDefKey } = useFillingMaterialContext()

  const userName = getUserInfo().userName
  const isApproval = getQuery('curTab') === 'approval'
  const lateStageTaskIds = ['userTask_initialReview', 'userTask_review']

  const isProjectManager = startUserName === userName
  const getShowDownload = () => {
    const isLaterStageOrClosed = (() => {
      if (isApproval) return true
      if (processStatus !== '1') return true
      return lateStageTaskIds.includes(curTaskDefKey)
    })()
    return isLaterStageOrClosed ? !isProjectManager : true
  }

  const [loading, setLoading] = useState(false)
  const showDownload = getShowDownload()
  const {
    projReviewMaterialsEnum,
    projPricingMaterialsEnum,
    projEstablishMaterialsEnum,
    projEstablishMaterialsApproveEnum,
    leaseFileTypeEnums,
    lendingMaterialType,
    materialsType,
    filingPolicy,
  } = App.getData().optionsType
  const childRefs = useRef({})
  const handleDownload = async (e) => {
    e.stopPropagation()
    setLoading(true)
    const fileIds = dataSource
      ?.reduce((accumulator, id, idx) => {
        const componentInstance = childRefs.current[idx]
        const currentKeys = componentInstance?.table?.getSelected()?.keys ?? []
        return [...accumulator, ...currentKeys]
      }, [])
      .filter((item) => typeof item === 'number')
    await Api.batchDownload({ id, tabCode: activeTab, moduleCode, fileIds })
    setLoading(false)
    console.log('批量下载')
  }
  const DownloadAll = showDownload && (
    <Button type="primary" size="small" onClick={handleDownload} loading={loading}>
      批量下载
    </Button>
  )
  const params = {
    id,
    moduleType: 'REFERENCE_MATERIALS',
  }

  const currentList = [
    ...projReviewMaterialsEnum,
    ...projPricingMaterialsEnum,
    ...leaseFileTypeEnums,
    ...projEstablishMaterialsEnum,
    ...projEstablishMaterialsApproveEnum,
    ...lendingMaterialType,
    ...materialsType,
    ...filingPolicy,
  ]

  const getLevelData = (itm) => {
    return (
      itm?.map((item, index) => {
        if (basic) {
          return {
            id: `folder-${index}`,
            key: item.materialName,
            enumType: item.key,
            value: item.materialList.map((i) => {
              return {
                id: i.recordId,
                name: i.filename,
                createByName: i.createByName,
                createTime: i.createTimestamp === -1 ? '' : moment(i.createTimestamp).format('YYYY-MM-DD HH:mm:ss'),
                idType: i.idType,
                businessType,
              }
            }),
          }
        } else {
          return {
            id: `folder-${index}`,
            key: item.key,
            enumType: item.key,
            value: item.value.map((i) => {
              return {
                id: i.id,
                name: i.filename,
                createByName: i.createByName,
                createTime: i.createTime,
                //idType: i.idType,
                businessType,
              }
            }),
          }
        }
      }) || []
    )
  }

  const renderTable = () => {
    return dataSource?.map((item, index) => {
      if (item.levelFileList) {
        return (
          <FileTable
            enumType={basic ? enumType[tableEnum[activeTab]] : currentList}
            params={params}
            ref={(el) => (childRefs.current[index] = el)}
            uploadType="rowUpload"
            title={<div className={'z-sub-title'}>{`${item.groupName}`}</div>}
            tableApi={() => getLevelData(item.levelFileList, index)}
            canBatchDownload={canBatchDownload}
            canDownload={canDownload}
            enableSelect={enableSelect}
            canEdit={canEdit}
            columns={columns}
            hideOperation
            foldKeys
          />
        )
      } else if (item.businessMaterialList) {
        const data =
          item.businessMaterialList?.map((item, index) => {
            return {
              id: `folder-${index}`,
              key: item.materialName,
              enumType: item.materialName,
              value: item.materialList.map((i) => {
                return {
                  id: i.recordId,
                  name: { value: i.filename },
                  createByName: i.createByName,
                  createTime: moment(i.createTimestamp).format('YYYY-MM-DD HH:mm:ss'),
                  idType: i.idType,
                  businessType,
                }
              }),
            }
          }) || []

        return (
          <FileTable
            enumType={currentList}
            params={params}
            ref={(el) => (childRefs.current[index] = el)}
            uploadType="rowUpload"
            title={<div className={'z-sub-title'}>{`${item.clientTypeName}(${item.name})`}</div>}
            tableApi={() => data}
            canBatchDownload={canBatchDownload}
            canDownload={canDownload}
            enableSelect={enableSelect}
            hideOperation
            canEdit={canEdit}
            columns={columns}
            foldKeys
          />
        )
      } else {
        return (
          <NoEnumFileTable
            tableApi={() => item.nonLevelfileListR}
            ref={(el) => (childRefs.current[index] = el)}
            canBatchDownload={canBatchDownload}
            canDownload={canDownload}
            title={item.groupName}
            enableSelect={enableSelect}
            canEdit={canEdit}
            params={params}
            columns={columns}
            hideOperation
          />
        )
      }
    })
  }

  return (
    <Collapse header={<h4 style={{ marginBottom: 0 }}>{name}</h4>} folded={folded} extra={DownloadAll} style={{ marginBottom: 16 }}>
      {(!dataSource || !dataSource.length) && <Empty />}
      {renderTable()}
    </Collapse>
  )
}

export default observer(Index)

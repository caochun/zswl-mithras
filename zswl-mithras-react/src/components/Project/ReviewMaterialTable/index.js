import { useEffect, useMemo, useState } from 'react'
import { FileTable } from '@/components/Table'
import { App, Button } from '@zswl/components'
import { history, getQuery } from '@zswl/admin'
import dataListApi from '@/api/common/materialsApi'

const nameMap = {
  PROJ_REVIEW: '项目评审资料',
  PROJ_ESTABLISH: '立项上传资料',
}
const ProjectCreateData = (props) => {
  const { mainId, name, businessType, businessMaterialList, clientTypeName, canEdit = true, isRiskManagerProj } = props

  const canEditType = ['CLIENT', 'PROJ_REVIEW'].includes(businessType)
  const canEditFlag = canEditType && canEdit

  const columns = [
    {
      title: '资料名称',
      dataIndex: 'name',
      fixed: 'left',
    },
  ]
  const { optionsType } = App.getData()
  const enumType = canEditFlag
    ? canEditFlag
    : [
        { value: 'PROJ_INFORMATION', label: '基本信息' },
        ...optionsType.normalMaterialsType,
        ...optionsType.materialsType,
        ...optionsType?.projEstablishMaterialsEnum,
        ...optionsType?.projEstablishMaterialsApproveEnum,
      ]
  const dataSource = useMemo(() => {
    return (
      businessMaterialList?.map((item, index) => {
        return {
          id: `folder-${index}`,
          key: item.materialName,
          value: item.materialList.map((i) => {
            return {
              id: i.recordId,
              name: i.filename,
              idType: i.idType,
              businessType,
            }
          }),
        }
      }) || []
    )
  }, [businessMaterialList])

  const params = {
    mainId,
    moduleType: 'PROJ_REVIEW',
  }
  return (
    <>
      <FileTable
        enumType={enumType}
        title={
          <div className={isRiskManagerProj ? 'z-group-title' : 'z-sub-title'}>
            {nameMap[businessType] || `${clientTypeName}(${name})`}
          </div>
        }
        canEdit={canEditFlag}
        uploadApi={({ file, fileType: materialsType }) =>
          dataListApi.postMaterialsUpload(
            {
              file,
              belongId: id,
              businessType: MODULE_TYPE,
              materialsType: v.value,
              materialsSubType: materialsType,
            },
            'materialsupload'
          )
        }
        columns={columns}
        tableApi={() => dataSource}
        canBatchDownload
        hasFormApproval={false}
        params={params}
      />
    </>
  )
}

export default ProjectCreateData

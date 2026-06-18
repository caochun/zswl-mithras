import { useEffect, useMemo, useState } from 'react'
import { FileTable } from '@/components'
import { App, Button } from '@zswl/components'

const nameMap = {
  PROJ_REVIEW: '项目评审资料',
  PROJ_ESTABLISH: '立项上传资料',
}
const ProjectCreateData = (props) => {
  const { mainId, name, businessType, businessMaterialList, clientTypeName, canEdit = true } = props
  const isProjectReview = businessType === 'PROJ_REVIEW'

  const columns = [
    {
      title: '资料名称',
      dataIndex: 'name',
      fixed: 'left',
    },
  ]
  const { optionsType } = App.getData()
  const enumType = [
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
          <div className={'z-sub-title'}>
            {nameMap[businessType] || `${clientTypeName}(${name})`}
          </div>
        }
        canEdit={isProjectReview && canEdit}
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

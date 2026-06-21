import { useMemo, useState } from 'react'
import { FileTable } from '@/components/Table'
import { App, Button } from '@zswl/components'

const ProjectEstablishmentDataTable = (props) => {
  const { mainId, name, businessType, businessMaterialList, clientTypeName, canEdit = true } = props

  const isProjectReview = businessType === 'PROJ_REVIEW'
  const isProjectData = businessType === 'PROJ_ESTABLISH'

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
    moduleType: 'PROJ_ESTABLISH',
  }
  return (
    <FileTable
      enumType={enumType}
      title={
        <div className={'z-sub-title'}>
          {isProjectData ? '立项上传资料' : `${clientTypeName}(${name})`}
        </div>
      }
      canEdit={isProjectReview && canEdit}
      columns={columns}
      tableApi={() => dataSource}
      canBatchDownload
      hasFormApproval={false}
      // uploadApi={({ file, fileType: materialsType }, config) => {
      //   return Api.postReportUpload({ file, groupCreditEstablishId: id, materialsType }, config)
      // }}
      params={params}
    />
  )
}

export default ProjectEstablishmentDataTable

import { useEffect, useMemo, useRef, useState } from 'react'
import { FileTable } from '@/components'
import { App } from '@zswl/components'
import { DateColumn } from '@/components/Format'
import { observer } from '@zswl/admin'

const ProjectCreateData = (props) => {
  const {
    mainId,
    name,
    businessType,
    businessMaterialList,
    clientTypeName,
    canEdit = true,
    getProjectDataDetail,
  } = props

  const isProjectData = businessType === 'GROUP_CREDIT_ESTABLISH_CLIENT'
  const ref = useRef(null)

  const columns = [
    {
      title: '资料名称',
      dataIndex: 'name',
      fixed: 'left',
    },
    { title: '文件类型', dataIndex: 'materialSubName' },
    { title: '上传人', dataIndex: 'createByName' },
    DateColumn({
      title: '上传时间',
      dataIndex: 'createTimestamp',
      dateFormat: 'YYYY-MM-DD HH:mm:ss',
    }),
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
              ...i,
              materialSubName: App.matchOption(enumType, item.key),
            }
          }),
        }
      }) || []
    )
  }, [businessMaterialList])
  console.log('dataSource: ', dataSource)

  const params = {
    mainId,
    moduleType: 'GROUP_CREDIT_ESTABLISH',
  }
  return (
    <FileTable
      enumType={enumType}
      title={<div className={'z-sub-title'}>{`${clientTypeName}(${name})`}</div>}
      canEdit={canEdit}
      columns={columns}
      canEditItem={false}
      tableApi={() => dataSource}
      canBatchDownload
      hasFormApproval={false}
      params={params}
      modalProps={{
        onCancel: () => {
          getProjectDataDetail()
          ref.current.table.search()
          ref.current.modal.close()
        },
      }}
      ref={ref}
      afterDelete={() => {
        getProjectDataDetail()
        ref.current.table.search()
      }}
    />
  )
}

export default observer(ProjectCreateData)

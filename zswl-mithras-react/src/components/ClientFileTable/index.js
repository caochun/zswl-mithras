import { observer } from '@zswl/admin'
import { FileTable } from '@/components/Table'
import { App } from '@zswl/components'
import { useEffect, useRef } from 'react'
import { toHump3 } from '@/utils'
import fileList from '@/utils/api/fileApi'
import styles from './index.less'
import { DateColumn } from '@/components/Format'

const nameMap = {
  PROJ_REVIEW: '项目评审资料',
  PROJ_ESTABLISH: '立项上传资料',
  GROUP_CREDIT_REVIEW: '授信评审资料',
  GROUP_CREDIT_ESTABLISH: '授信上传资料',
}
const Report = ({
  mainId,
  canEdit = true,
  item,
  getProjectDataDetail,
  uploadModule = 'PROJ_REVIEW',
  isRiskManagerProj,
  commentGuideLine,
}) => {
  const {
    clientType,
    id,
    clientTypeName,
    clientId,
    name,
    businessMaterialList = [],
    businessType,
  } = item
  const ref = useRef([])
  const {
    corporationClientMaterialTypeEnum,
    normalClientMaterialTypeEnum,
    groupCreditEstablishMaterialsEnum,
  } = App.getData().optionsType
  const currentList =
    clientType == 'CORPORATION' ? corporationClientMaterialTypeEnum : normalClientMaterialTypeEnum
  const columns = [
    { title: '资料清单', dataIndex: 'name' },
    { title: '文件类型', dataIndex: 'materialSubName' },
    { title: '上传人', dataIndex: 'createByName' },
    DateColumn({
      title: '上传时间',
      dataIndex: 'createTimestamp',
      dateFormat: 'YYYY-MM-DD HH:mm:ss',
    }),
  ]

  const params = {
    mainId: uploadModule === 'PROJ_PRICING' ? mainId : id,
    moduleType: uploadModule,
  }
  const { optionsType } = App.getData()
  const listFormat = (res) => {
    const list =
      res?.map((item, index) => {
        const labelCurrent = [
          ...currentList,
          ...optionsType.normalMaterialsType,
          ...optionsType.materialsType,
          ...optionsType.projEstablishMaterialsEnum,
          ...groupCreditEstablishMaterialsEnum,
        ]
        const { label, childSelectName } = App.matchOption(labelCurrent, item.materialName)
        return {
          id: `folder-${index}`,
          enumType: item.materialName,
          name: label,
          children: item.materialList
            .map((i) => {
              return {
                ...i,
                id: i.recordId,
                name: i.filename,
                idType: i.idType,
                materialsType: i.materialSubName,
                materialSubName: App.matchOption(childSelectName, i.materialSubName).label,
                businessType,
              }
            })
            .sort((a, b) => {
              const optionsList = App.getData().optionsType[childSelectName] ?? []
              return (
                optionsList?.findIndex((v) => v.value === a.materialsType) -
                optionsList?.findIndex((v) => v.value === b.materialsType)
              )
            }),
        }
      }) || []

    const lossEnum = (currentList ?? [])
      .filter((v) => !list.find((i) => i.enumType === v.value))
      .map(({ childSelectName, label, value }, index) => ({
        id: `folder-${index + list.length}`,
        enumType: value,
        name: label,
        childSelectName,
        children: [],
      }))
    return [...list, ...lossEnum].filter(
      (v) => !['LEASE_APPLICATION', 'CREDIT_LETTER'].includes(v.enumType)
    )
  }
  const getNewList = async () => {
    const res = await getProjectDataDetail(mainId)
    setTimeout(() => {
      const newList = res?.find((v) => v.clientId == item.clientId)?.businessMaterialList ?? []
      const newListFormat = listFormat(newList)
      ref.current.table.setList(newListFormat)
      ref.current.setExpandKeys(newListFormat?.map(({ id }, index) => id))
    }, 100)
  }
  useEffect(() => {
    getNewList()
  }, [mainId])
  return (
    <div className={styles.fileTable}>
      <FileTable
        enumType={currentList}
        title={
          <div className={'z-sub-title'}>
            {nameMap[businessType] || `${clientTypeName}(${name})`}
          </div>
        }
        uploadType="rowUpload"
        size="small"
        canEditItem={false}
        listFormat={listFormat}
        handleEnumType={(options) =>
          options?.filter(
            (item) =>
              ![
                'BUSINESS_INFORMATION_UNGROUPED',
                'BASIC_INFORMATION_UNGROUPED',
                'FINANCIAL_INFORMATION_UNGROUPED',
              ].includes(item.value)
          )
        }
        uploadApi={async ({ file, fileType, enumType: materialsType }) =>
          await fileList.postFileUpload(
            {
              file,
              mainId: id,
              moduleType: businessType,
              materialsType,
              materialsSubType: fileType,
              sourceBusinessKey: clientId,
            },
            `${toHump3(uploadModule)}FileUpload`
          )
        }
        params={{ ...params }}
        tableApi={() => businessMaterialList}
        modalProps={{
          onCancel: () => {
            getNewList()
            ref.current.modal.close()
          },
        }}
        canEdit={canEdit}
        canDelete={(record) => canEdit && !!record.sourceBusinessKey}
        columns={columns}
        ref={ref}
        afterDelete={() => {
          getNewList()
        }}
        selectable={{
          checkStrictly: false,
          getCheckboxProps: (record) => {
            return {
              disabled: record.name?.changeType === 'REMOVE',
            }
          },
        }}
        isRiskManagerProj={isRiskManagerProj}
        itemProps={item}
        commentGuideLine={commentGuideLine}
      />
    </div>
  )
}
export default observer(Report)

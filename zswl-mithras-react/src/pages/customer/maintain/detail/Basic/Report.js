import styles from '../index.less'
import { observer } from '@zswl/admin'
import { FileTable } from '@/components'
import dataListApi from '@/api/common/dataList'
import { App, Button } from '@zswl/components'
import _ from 'lodash'

const MODULE_TYPE = 'CLIENT'
const Report = ({ id, canEdit = true, businessVersion, type, processInstanceId }) => {
  const columns = [
    { title: '资料清单', dataIndex: 'name' },
    { title: '文件类型', dataIndex: 'materialSubTypeName' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const { corporationClientMaterialTypeEnum, normalClientMaterialTypeEnum } =
    App.getData().optionsType
  const currentList =
    type == 'CORPORATION' ? corporationClientMaterialTypeEnum : normalClientMaterialTypeEnum
  const params = {
    mainId: id,
    moduleType: MODULE_TYPE,
    businessVersion,
    ext: {
      processInstanceId,
    },
  }

  const listFormat = (res) => {
    const list =
      res?.map((item, index) => {
        const { label, childSelectName } = App.matchOption(currentList, item.key)
        return {
          id: `folder-${index}`,
          enumType: item.key,
          name: label,
          children: item.value
            ?.map((i) => {
              return {
                ...i,
                name: i.filename,
                materialsType: i.materialSubName,
                materialsTypeName: App.matchOption(childSelectName, i.materialSubName)?.label,
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
    const lossEnum = currentList
      .filter((v) => !list.find((i) => i.enumType === v.value))
      .map(({ childSelectName, label, value }, index) => ({
        id: `folder-${index + list.length}`,
        enumType: value,
        name: label,
        childSelectName,
        children: [],
      }))
    return [...list, ...lossEnum]
  }
  return (
    <div className={styles.fileTable}>
      <FileTable
        enumType={currentList}
        size="small"
        uploadType="rowUpload"
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
        listFormat={(res) => listFormat(res)}
        uploadApi={({ file, fileType, enumType: materialsType }) =>
          dataListApi.postMaterialsUpload(
            {
              file,
              belongId: id,
              businessType: MODULE_TYPE,
              materialsType,
              materialsSubType: fileType,
            },
            'materialsupload'
          )
        }
        params={{ ...params, materialsTypes: currentList.map((v) => v.value) }}
        title={
          <h3 className={styles.title} style={{ marginBottom: 0 }}>
            资料清单
          </h3>
        }
        canEdit={canEdit}
        columns={columns}
        selectable={{
          checkStrictly: false,
          getCheckboxProps: (record) => {
            return {
              disabled: record.name?.changeType === 'REMOVE',
            }
          },
        }}
      />
    </div>
  )
}
export default observer(Report)

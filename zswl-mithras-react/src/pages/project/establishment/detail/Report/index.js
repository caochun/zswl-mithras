import styles from '../index.less'
import { observer } from '@zswl/admin'
import Api from './api'
import { FileTable } from '@/components'
import { App, Button } from '@zswl/components'
import Store from './store'
import { useMemo } from 'react'

const MODULE_TYPE = 'PROJ_ESTABLISH'
const Report = ({ id: mainId, businessVersion, rootStore, canEdit }) => {
  const store = useMemo(() => new Store({ rootStore }), [rootStore])

  const columns = [
    { title: '资料清单', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const params = {
    mainId,
    moduleType: MODULE_TYPE,
    businessVersion,
  }
  const { optionsType } = App.getData()

  return (
    <FileTable
      enumType={[
        ...optionsType.projEstablishMaterialsEnum,
        ...optionsType.projEstablishMaterialsApproveEnum,
      ]}
      handleEnumType={() => optionsType.projEstablishMaterialsEnum}
      uploadApi={({ file, fileType: materialsType }) =>
        Api.postReportUpload({
          file,
          projEstablishId: mainId,
          materialsType,
        })
      }
      extra={[
        <Button
          type="primary"
          disabled={!canEdit}
          key="generate"
          onClick={() => store.generate(mainId)}
        >
          生成报告
        </Button>,
      ]}
      params={params}
      title={<h4 className={styles.title}>项目立项资料</h4>}
      canEdit={canEdit}
      columns={columns}
    />
  )
}
export default observer(Report)

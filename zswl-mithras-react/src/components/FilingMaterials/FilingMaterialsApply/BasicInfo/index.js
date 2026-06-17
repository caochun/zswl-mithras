import { observer } from '@zswl/admin'
import CollapseTable from '../CollapseTable'
import BasicCollapseTable from '../BasicCollapseTable'
import { REFERENCE_MATERIALS, OPERATIONAL_REVIEW } from '../enum'

const Index = ({ id, data, canEdit = false }) => {
  const renderBasicTable = () => {
    return (
      <BasicCollapseTable
        id={id}
        canEdit={false}
        name="参考资料"
        moduleCode={REFERENCE_MATERIALS}
        canBatchDownload={false}
        canDownload={false}
        enableSelect={true}
        dataSource={data?.[0]?.projMaterialsListListRSP}
        folded={true}
        basic
      />
    )
  }

  const renderArchiveTable = () => {
    return data?.[1]?.projMaterialsListListRSP
      ?.filter((item) => !!item.businessMaterialList)
      .map((item) => {
        return (
          <CollapseTable
            id={id}
            key={id}
            collapseName={`归档资料-${item.name}`}
          canEdit={true}
          canBatchDownload={false}
            enableSelect={true}
            moduleCode={OPERATIONAL_REVIEW}
            dataSource={item.businessMaterialList}
            clientId={item.clientId}
            custDirEnum={item.custDirEnum}
            basic
            {...item}
          />
        )
      })
  }
  return (
    <>
      {renderBasicTable()}
      {renderArchiveTable()}
    </>
  )
}

export default observer(Index)

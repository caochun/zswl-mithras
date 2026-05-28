import { observer } from '@zswl/admin'
import CollapseTable from '../CollapseTable'
import BasicCollapseTable from '../BasicCollapseTable'
import { REFERENCE_MATERIALS, OPERATIONAL_REVIEW, tableEnum, TEMPLATE_LIST } from '../enum'
import { useFillingMaterialContext } from '../Context'

const Index = ({ id, data, store }) => {
  const { activeTab } = useFillingMaterialContext()
  const renderBasicTable = () => {
    return (
      <BasicCollapseTable
        id={id}
        canEdit={false}
        name="参考资料"
        canBatchDownload={false}
        canDownload={false}
        enableSelect={true}
        moduleCode={REFERENCE_MATERIALS}
        dataSource={data?.[0]?.filingMaterialsGroupQueryRSPList}
        basic={false}
        folded={true}
      />
    )
  }

  const renderArchiveTable = () => {
    return data?.[1]?.filingMaterialsGroupQueryRSPList?.map((item) => {
      return (
        <CollapseTable
          id={id}
          key={id}
          enableSelect={true}
          collapseName={item.groupName}
          canBatchDownload={false}
          moduleCode={OPERATIONAL_REVIEW}
          dataSource={item.levelFileList || item.nonLevelfileListR}
          businessType={item.levelFileList?.[0].value?.[0].businessType}
          basic={false}
          custDirEnum={tableEnum[activeTab]}
          canEdit={true}
          store={store}
          {...item}
        />
      )
    })
  }
  return (
    <>
      {activeTab !== TEMPLATE_LIST[4].value && renderBasicTable()}
      {renderArchiveTable()}
    </>
  )
}

export default observer(Index)

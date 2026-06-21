import { observer } from '@zswl/admin'
import { Button } from '@zswl/components'
import { UnorderedListOutlined } from '@ant-design/icons'
import DashboardRadioTabs from '../../../RadioTabs'
import RankingDrawer from './RankingDrawer/AssetsRankingDrawer'
import RankingTable from './RankingTable/AssetsRankingTable'

const Index = ({ store }) => {
  const { currentTab, allRankingDrawer } = store

  return (
    <div>
      <DashboardRadioTabs
        tabBarExtraContent={
          currentTab === 'province' && (
            <Button icon={<UnorderedListOutlined />} onClick={allRankingDrawer.open}>
              区域排名明细
            </Button>
          )
        }
        defaultActiveKey="province"
        onChange={(value) => {
          store.setCurrentTab(value)
        }}
        items={[
          {
            label: '省份资产排名',
            key: 'province',
            children: (
              <RankingTable store={store.provinceRankingTable} key={'province'} type="province" />
            ),
          },
          {
            label: '经济圈排名',
            key: 'area',
            children: <RankingTable store={store.areaRankingTable} key={'area'} type="area" />,
          },
        ]}
      ></DashboardRadioTabs>
      <RankingDrawer store={store}></RankingDrawer>
    </div>
  )
}

export default observer(Index)

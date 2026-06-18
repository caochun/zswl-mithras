import { observer } from '@zswl/admin'
import { Button } from '@zswl/components'
import { UnorderedListOutlined } from '@ant-design/icons'
import RadioTabs from '@/components/RadioTabs'
import RankingDrawer from './RankingDrawer'
import RankingTable from './RankingTable'

const Index = ({ store }) => {
  const { currentTab, allRankingDrawer } = store

  return (
    <div>
      <RadioTabs
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
      ></RadioTabs>
      <RankingDrawer store={store}></RankingDrawer>
    </div>
  )
}

export default observer(Index)

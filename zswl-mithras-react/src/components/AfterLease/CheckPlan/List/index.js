import { Tabs } from 'antd'
import { useEffect, useState } from 'react'
import { Page, Access } from '@zswl/components'
import { observer } from '@zswl/admin'
import CheckList from './Tab/CheckList'
import OpenList from './Tab/OpenList'
import Strategy from './Tab/Strategy'

function Index({ query: { tab } }) {
  const [curTab, setCurTab] = useState('1')
  const hasStrategy = Access.validate('afterleaseCheckplanAssetstrategy')

  useEffect(() => {
    tab && setCurTab(tab)
  }, [tab])
  return (
    <Page>
      <Tabs
        onChange={setCurTab}
        activeKey={curTab}
        items={[
          { label: '租后检查计划', key: '1', children: <CheckList /> },
          { label: '外部公开信息查询表', key: '2', children: <OpenList /> },
          hasStrategy && { label: '资产管理策略', key: '3', children: <Strategy /> },
        ].filter(Boolean)}
      ></Tabs>
    </Page>
  )
}

export default observer(Index)

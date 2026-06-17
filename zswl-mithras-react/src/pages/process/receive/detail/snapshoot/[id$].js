import React from 'react'
import { Page } from '@zswl/components'
import { observer } from '@zswl/admin'
import SnapShoot from '@/components/Process/Snapshoot'

const Index = ({ params: { id } }) => {
  return (
    <Page header={null}>
      <SnapShoot id={id}></SnapShoot>
    </Page>
  )
}

export default observer(Index)

import React from 'react'
import { Page } from '@zswl/components'
import { observer } from '@zswl/admin'
import { ProcessSnapshoot as SnapShoot } from '@/components/Process/ProcessEntries'

const Index = ({ params: { id } }) => {
  return (
    <Page header={null}>
      <SnapShoot id={id}></SnapShoot>
    </Page>
  )
}

export default observer(Index)

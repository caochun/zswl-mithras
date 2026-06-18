import { Button, Page, Table } from '@zswl/components'
import { http, observer } from '@zswl/admin'
import { Card, Col, Row } from 'antd'
import TenNationalDebt from './TenNationalDebt'
import LPRTable from './LPRTable'
import Financing from './Financing'
import Guarantee from './Guarantee'
import Api from '../api'
import newFtpTreasuryBondYieldApi from '@/api/newFtp/newFtpTreasuryBondYieldApi'
import newFtpShiborInterestRateApi from '@/api/newFtp/newFtpShiborInterestRateApi'

function Index({ path, mainId, businessVersion, canEdit = true }) {
  const params = {
    mainId,
    version: businessVersion,
  }
  const getTreasury = async (tableProps) => {
    const func = canEdit ? Api.postBondDetail : Api.postBondCompare
    return await func({ ...tableProps, ...params })
  }
  const getShibor = async (tableProps) => {
    const func = canEdit ? Api.postShiborDetail : Api.postShiborCompare
    return await func({ ...tableProps, ...params })
  }
  const getLPR = async (tableProps) => {
    const func = canEdit ? Api.postLprDetail : Api.postLprCompare
    return await func({ ...tableProps, ...params })
  }

  return (
    <Page>
      <Financing canEdit={canEdit} params={params} />
      <Guarantee params={params} canEdit={canEdit} />
      <TenNationalDebt
        loadApi={getTreasury}
        title="十年期国债收益率"
        uploadApi={(file) => newFtpTreasuryBondYieldApi.postYieldImport({ ...file, mainId })}
        canEdit={canEdit}
      />
      <TenNationalDebt
        loadApi={getShibor}
        title="一年期SHIBOR利率"
        uploadApi={(file) => newFtpShiborInterestRateApi.postRateImport({ ...file, mainId })}
        canEdit={canEdit}
      />
      <LPRTable loadApi={getLPR} canEdit={canEdit} />
    </Page>
  )
}

export default observer(Index)

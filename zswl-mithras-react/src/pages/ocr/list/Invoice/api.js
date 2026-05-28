import { http } from '@zswl/admin'

export default {
  postLeaseVatInvoiceLocked: (params) => http.post('/lease/vatInvoice/locked', params),
}

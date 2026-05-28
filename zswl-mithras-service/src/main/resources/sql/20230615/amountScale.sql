update corp_bond_info
set issue_total    = issue_total / 100000000,
    stock_scale    = stock_scale / 100000000,
    maturity_scale = maturity_scale / 100000000;
update corp_bond_info_lib
set issue_total    = issue_total / 100000000,
    stock_scale    = stock_scale / 100000000,
    maturity_scale = maturity_scale / 100000000;
update corp_related_enterprise
set register_capital=register_capital / 10000,
    invest_amount   = invest_amount / 10000;
update corp_related_enterprise_lib
set register_capital=register_capital / 10000,
    invest_amount   = invest_amount / 10000;
update corp_shareholder_info
set paid_total        = paid_total / 10000,
    actual_paid_total = actual_paid_total / 10000;
update corp_shareholder_info_lib
set paid_total        = paid_total / 10000,
    actual_paid_total = actual_paid_total / 10000;
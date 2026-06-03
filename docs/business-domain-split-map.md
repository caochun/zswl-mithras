# Business Domain Split Map

This map records the target vertical business modules for code still living in
`zswl-mithras-service` or old `zswl-mithras-domain-*` modules.

## Target Modules

| Module | Main service roots | Notes |
| --- | --- | --- |
| `zswl-mithras-customer` | `client` | Customer/counterparty capability. Keep separate from login/account identity. |
| `zswl-mithras-identity` | `auth`, user/account-facing system pieces | Identity, authentication, authorization and account lifecycle. |
| `zswl-mithras-project` | `projestablish`, `projreview`, `projpricing`, `projlifecycle`, `projfms`, `project` | Project lifecycle capability. |
| `zswl-mithras-workflow` | `flow`, `process` | Workflow/process orchestration. |
| `zswl-mithras-document` | `materialsfile`, `file`, `gendoc`, `genhtml`, `filingmaterials` document rendering parts | Document generation, templates, materials and file-facing document services. |
| `zswl-mithras-contract` | `contract`, `contractcp` | Contract lifecycle. Overdue code currently remains until contract/afterlease cycles are broken. |
| `zswl-mithras-afterlease` | `afterlease`, `afterlese`, `collection`, overdue collection/litigation | After-lease operations, collection and litigation. |
| `zswl-mithras-riskcontrol` | `riskcontrol`, `assetclassify` risk-facing pieces | Risk control decisions, rules, checks and risk workflows. |
| `zswl-mithras-ftp` | `ftp`, `newftp` | Funds transfer pricing. |
| `zswl-mithras-third` | `third`, external provider clients | Third-party integrations and anti-corruption layers. |
| `zswl-mithras-capital` | `capital` | Capital flow and write-off processing. |
| `zswl-mithras-fund` | `fund`, fund-direct pieces | Funding institution, credit and guarantee capability. |
| `zswl-mithras-finance` | `finance`, `financeprojectdistribution`, `financeprofitdistribution` | Finance/accounting settlement and profit distribution. |
| `zswl-mithras-payment` | `payment` | Payment application, write-off and payment materials. |
| `zswl-mithras-budget` | `budget` | Budget planning, execution and ECL prediction budget workflows. |
| `zswl-mithras-archives` | `archives` | Archive management. |
| `zswl-mithras-assetclassify` | `assetclassify` | Asset classification workflow and ledgers. |
| `zswl-mithras-leaseholdproperty` | `leaseholdproperty`, `property` | Leasehold property, vehicles, invoices and OCR. |
| `zswl-mithras-liquidity` | `liquiditymanage`, `liquidityrisk`, `margin` | Liquidity management, liquidity risk, margins and warranties. |
| `zswl-mithras-credit` | `groupcreditestablish`, `groupcreditreview`, `creditreport`, credit limit pieces | Group credit, credit reports and credit limits. |
| `zswl-mithras-kpi` | `kpi` | KPI and performance management. |
| `zswl-mithras-workbench` | `workbench` | Workbench cards, shortcuts and operational widgets. |
| `zswl-mithras-dashboard` | `dashboard` | Dashboards and cross-domain read models. |
| `zswl-mithras-policy` | `policy` | Policy ledger, policy versions and policy drafts. |
| `zswl-mithras-filingmaterials` | `filingmaterials` non-document business workflow | Filing materials workflow. Document rendering stays in document. |

## Package Layout

Each vertical module should converge on:

| Package | Responsibility |
| --- | --- |
| `application` | Use cases, orchestration inside the business capability, application ports. |
| `domain` | Domain concepts, domain services, enums and rules. |
| `infrastructure` | Persistence, external adapters, framework configuration. |
| `interfaces` | Controllers, job entry points, message consumers and API-facing DTOs. |

Avoid recreating old horizontal roots such as `service/controller/mapper` inside a vertical module.

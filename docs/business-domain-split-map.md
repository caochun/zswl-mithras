# Business Domain Split Map

This map records the target vertical business modules used to retire the old
horizontal `zswl-mithras-service` and `zswl-mithras-domain-*` modules.

## Module Responsibilities

| Module | Responsibility | Belongs Here | Does Not Belong Here |
| --- | --- | --- | --- |
| `zswl-mithras-api` | Shared API contracts and DTOs. | Request/response DTOs, public API interfaces, validation annotations. | Business services, persistence, controller implementations. |
| `zswl-mithras-foundation` | Shared technical foundation used by most modules. | Common utilities, framework configuration, async task base records, operation logs, generic infrastructure helpers. | Business rules, cross-domain orchestration, module-specific services. |
| `zswl-mithras-application` | Application composition layer across business domains. | Cross-domain facades, adapters, process end handlers, document/render orchestration, glue that needs multiple business modules. | Pure single-domain business logic that can live in one domain module. |
| `zswl-mithras-web` | Bootstrapping and web application entry. | Spring Boot entry point, top-level web startup wiring. | Business controllers or service implementations. |
| `zswl-mithras-report` | Report assembly and export entry points that depend on application composition. | Report controllers, board/report export assembly. | Domain calculations or single-domain query logic. |
| `zswl-mithras-system` | System governance and platform administration. | Identity/auth, users, account lifecycle, dictionaries, config, switches, OnlyOffice, storage, plugins, project code, audit/maintenance. | Customer/counterparty business data, business workflow decisions. |
| `zswl-mithras-basedata` | Business reference data. | Regions, institutions, common base dictionaries/reference tables used by business modules. | Runtime system config or user identity. |
| `zswl-mithras-customer` | Customer/counterparty domain. | Client/customer profiles, unified customer data, customer copies, customer material relations. | Login account identity, system users, credit decisions. |
| `zswl-mithras-projectprocess` | Project establishment, review, pricing and lifecycle. | `projestablish`, `projreview`, `projpricing`, project lifecycle events, project process state machines. | Contract execution, after-lease operations, finance settlement. |
| `zswl-mithras-workflow` | Workflow engine application layer. | Flow/process APIs, process preparation primitives, workflow utilities and callbacks. | Business-specific process handlers unless they are generic workflow capabilities. |
| `zswl-mithras-document` | Generic document and material file capability. | File records, material lists, document templates, generic render/generation infrastructure, OCR-facing document services. | Filing-material business approval flow, domain-specific report semantics. |
| `zswl-mithras-filingmaterials` | Filing materials business workflow. | Filing application, filing process, filing material business rules. | Generic file storage, generic document generation. |
| `zswl-mithras-archives` | Archive management. | Archive templates, archive records, archive jobs and archive-facing controllers. | Filing approval flow or generic document rendering. |
| `zswl-mithras-contract` | Contract lifecycle and contract data. | Contract base info, contract files, contract rent/cashflow, contract effect checks, contract process handlers. | Project review/pricing, after-lease collection, payment execution. |
| `zswl-mithras-payment` | Payment applications and payment execution. | Payment plans, payment actuals, write-off/payment materials, payment process state. | Collection, finance accounting, capital fund-flow processing. |
| `zswl-mithras-collection` | Collection and receivable collection capability. | Collection plans, overdue collection records, collection payment coordination. | Broad after-lease management, contract creation, finance settlement. |
| `zswl-mithras-afterlease` | After-lease operations. | After-lease checks, adjustments, rent collection operations, post-lease ledgers, litigation/collection operations owned by after-lease. | Generic collection primitives if reusable by non-after-lease modules. |
| `zswl-mithras-credit` | Group credit and credit limit. | Group credit establishment/review, credit limit records, credit limit occupation/release/change logic. | External credit report query/parsing, rating/model calculation. |
| `zswl-mithras-creditreport` | Credit report acquisition and report data. | Credit report queries, parsers, report controllers, report persistence, third-party credit report integration adapters. | Credit limit lifecycle or group credit decision workflow. |
| `zswl-mithras-rating` | Rating and model calculation. | Rating client/amount models, rating snapshots, model indicators, decision integration. | Credit limit occupation, association report output. |
| `zswl-mithras-associationreport` | Association report domain. | Association report data collection, report libraries, report jobs, report business situation data. | Rating model calculation or dashboard read models. |
| `zswl-mithras-riskcontrol` | Risk control domain. | Risk strategies, scorecards, concentration risk, risk metrics, blacklist/graylist-facing checks. | Generic metrics aggregation, asset classification ledger ownership. |
| `zswl-mithras-blackgray` | Black/gray list capability. | Black/gray list records, list checks, Redis-backed list services, third-party list sync. | General risk strategy orchestration. |
| `zswl-mithras-assetclassify` | Asset classification domain. | Asset classification workflows, classification ledgers, related process handlers. | Generic risk strategy, collection, payment. |
| `zswl-mithras-fund` | Funding institution and fund business. | Fund organizations, fund credit, guarantees, direct financing, fund receipt/repay, fund process state. | Capital flow accounting, FTP pricing. |
| `zswl-mithras-capital` | Capital flow processing. | Bank/capital flow records, fund collect/payment flow processing, write-off support. | Payment application lifecycle or finance settlement ledgers. |
| `zswl-mithras-finance` | Finance/accounting settlement and distribution. | Finance settlement, monthly management, finance project distribution, profit/distribution records, finance reports. | Capital raw flow ingestion, payment application approval. |
| `zswl-mithras-budget` | Budget planning and ECL prediction workflow. | Budget base data, execution, ECL prediction, budget process handlers. | KPI performance management or rating model services. |
| `zswl-mithras-ftp` | Funds transfer pricing. | Old/new FTP pricing, monthly/quarterly guidance, FTP versioning and pricing libraries. | Payment execution, capital flow write-off. |
| `zswl-mithras-liquidity` | Liquidity management and liquidity risk. | Liquidity indicators, account balances, liquidity risk calculation and data services. | Margin records, unless directly part of liquidity risk views. |
| `zswl-mithras-margin` | Margin and warranty records. | Margin base info, margin records, warranty records, margin support ports. | Liquidity indicators or risk concentration strategy. |
| `zswl-mithras-leaseholdproperty` | Leasehold property collateral/assets. | Lease items, vehicles, invoices, appraisal whitelist, leasehold OCR/review support. | Generic customer or contract data. |
| `zswl-mithras-policy` | Policy ledger and policy versioning. | Policy info, drafts, versions, policy comparison/process support. | Payment, contract, or project approval logic. |
| `zswl-mithras-kpi` | KPI and performance management. | KPI project distribution, performance records, KPI calculations, KPI process handlers. | Generic metric read models or dashboards. |
| `zswl-mithras-metric` | Cross-domain metrics/read-model calculation. | Metric factors, financial-cloud metrics, cross-domain aggregation jobs and metric calculators. | Domain-owned operational records or UI dashboard composition. |
| `zswl-mithras-dashboard` | Dashboard read models. | Dashboard queries, dashboard exports, cross-domain dashboard aggregations. | Domain write logic, reusable metric calculation primitives. |
| `zswl-mithras-workbench` | User workbench and operational widgets. | Workbench cards, shortcuts, radar/bar chart widgets, workbench jobs. | Domain metric ownership or dashboard pages. |
| `zswl-mithras-third` | Third-party integration anti-corruption layer. | External provider clients, data share, EP, Xinsight, Tianyancha/QCC/Providence/Jinkong/financial-share adapters. | Business workflows that merely call third-party services. |
| `zswl-mithras-message` | Messaging and notification. | Message records, message dispatch, email/SMS/notification handlers, workflow notifications. | Business events that own domain state changes. |

`zswl-mithras-react` is not part of the Maven reactor. Treat it as a frontend/static
asset module unless it is explicitly reintroduced into the backend build.

## Dependency Direction

| Layer | Modules | Dependency Rule |
| --- | --- | --- |
| Entry | `zswl-mithras-web`, `zswl-mithras-report` | May depend on application composition. Should not own business rules. |
| Composition | `zswl-mithras-application` | May depend on many domain modules to orchestrate use cases. Domain modules should not depend on it. |
| Domain capabilities | Customer, project, contract, payment, finance, fund, risk, credit, after-lease and related business modules | Prefer owning their own rules, persistence and single-domain use cases. Cross-domain calls should go through ports/adapters where possible. |
| Horizontal business capabilities | `workflow`, `document`, `third`, `message`, `metric`, `dashboard`, `workbench` | Shared business-facing capabilities. Keep their responsibilities narrow and avoid becoming a replacement service module. |
| Foundation | `api`, `foundation`, `basedata`, `system` | Lowest-level shared contracts, technical foundation, reference data and platform administration. Avoid upward dependencies on business composition. |

## Current Boundary Debt

These are known places where the module responsibility is clear, but package layout
or dependencies still show migration residue:

| Area | Current State | Target Direction |
| --- | --- | --- |
| `service` package roots inside domain modules | Many migrated files still keep old `cn.zswltech.mithras.service.*` packages. | Rename gradually into the owning module package when touching each domain. |
| `zswl-mithras-application` | Still contains a large amount of old service orchestration. | Keep only cross-domain composition; push single-domain logic back to the owning module. |
| `zswl-mithras-report` | Depends on `application`, so it is an entry/report assembly layer. | Avoid adding domain calculations here; calculations should live in domain/metric modules. |
| `metric`, `dashboard`, `workbench` | All are cross-domain read-side capabilities, but at different levels. | `metric` owns reusable calculations, `dashboard` owns dashboard read models, `workbench` owns user-facing widgets. |
| `document`, `filingmaterials`, `archives` | Related to files/materials but represent different responsibilities. | Keep document infrastructure, filing workflow and archive management separate unless a concrete cycle forces a smaller refactor. |
| `credit`, `creditreport`, `rating`, `associationreport` | Related to credit/risk information but each has a distinct owner. | Keep credit limit/group credit, external credit reports, rating models and association reports separate. |
| `finance`, `capital`, `payment`, `fund`, `ftp`, `liquidity`, `margin` | Financially related but represent different lifecycle stages. | Merge only when ownership and write model are the same; otherwise use ports/adapters. |

## Package Layout

Each vertical module should converge on:

| Package | Responsibility |
| --- | --- |
| `application` | Use cases, orchestration inside the business capability, application ports. |
| `domain` | Domain concepts, domain services, enums and rules. |
| `infrastructure` | Persistence, external adapters, framework configuration. |
| `interfaces` | Controllers, job entry points, message consumers and API-facing DTOs. |

Avoid recreating old horizontal roots such as `service/controller/mapper` inside a vertical module.

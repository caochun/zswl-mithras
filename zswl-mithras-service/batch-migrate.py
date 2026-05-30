#!/usr/bin/env python3
"""
Batch migration script: replace direct fund service injection with FundFacade.

Usage:
    python3 batch-migrate.py file1.java file2.java ...
    python3 batch-migrate.py --all   # auto-discover all files with fund service imports
"""

import re
import sys
import os

# ============================================================
# Configuration: which service types map to which FundFacade imports
# ============================================================

FUND_FACADE_IMPORT = "import cn.zswltech.mithras.service.facade.fund.FundFacade;"

# Import patterns to remove (service packages)
FUND_SERVICE_IMPORT_PREFIXES = [
    "import cn.zswltech.mithras.service.service.fund.",
    "import cn.zswltech.mithras.service.fund.direct.service.",
]

# All service class names that should be replaced by FundFacade
# Maps: ServiceClassName -> category for method-call rewriting
SERVICE_CATEGORIES = {
    # Organization
    "FundOrganizationService": "organization",
    # Financing (indirect)
    "FundFinancingBaseInfoService": "financing",
    "FundFinancingService": "financing_aggregate",
    "FundFinancingPlanService": "financing_plan",
    "FundFinancingFeeDetailService": "financing_fee",
    "FundFinancingRepayActualService": "financing_repay_actual",
    "FundFinancingRepayEstimateService": "financing_repay_estimate",
    "FundFinancingCollectAccountService": "financing_collect_account",
    "FundFinancingPayAccountService": "financing_pay_account",
    "FundFinancingPledgeInfoService": "financing_pledge",
    "FundFinancingEarlySettlePlanService": "financing_early_settle",
    "FundFinancingCreditRefService": "credit_ref",
    # Direct financing
    "FundDirectFinancingBaseInfoService": "direct_financing",
    "FundDirectFinancingFeeDetailService": "direct_financing_fee",
    "FundDirectFinancingRepayActualService": "direct_financing_repay_actual",
    "FundDirectFinancingRepayActualSplitService": "direct_financing_repay_split",
    "FundDirectFinancingRepayActualSplitRecordService": "direct_financing_repay_split_record",
    "FundDirectFinancingPledgeInfoService": "direct_financing_pledge",
    "FundDirectFinancingProductDetailService": "direct_financing_product",
    "FundDirectFinancingSubscriptionDetailService": "direct_financing_subscription",
    "FundDirectFinancingCollectAccountService": "direct_financing_collect_account",
    "FundDirectFinancingPayAccountService": "direct_financing_pay_account",
    "FundDirectFinancingAssetPoolService": "direct_financing_asset_pool",
    "FundDirectFinancingPropertyService": "direct_financing_property",
    # Credit
    "FundCreditService": "credit",
    "FundCreditGuaranteeDetailService": "credit_guarantee_detail",
    # Guarantee
    "FundGuaranteeAgencyService": "guarantee_agency",
    "FundGuaranteeInfoService": "guarantee_info",
    # Receipt/Repay
    "FundReceiptRepayBaseInfoService": "receipt_repay",
    "FundReceiptRepayBorrowingService": "receipt_repay_borrowing",
    "FundReceiptRepayCashFlowService": "receipt_repay_cashflow",
    "FundReceiptRepayCashDepositService": "receipt_repay_deposit",
    "FundReceiptRepayExpenseService": "receipt_repay_expense",
    "FundReceiptRepayVersionService": "receipt_repay_version",
    "FundReceiptRepayStateService": "receipt_repay_state",
    "FundReceiptFlowDetailService": "receipt_flow_detail",
    "FundReceiptFlowPlanService": "receipt_flow_plan",
    "FundReceiptAccountService": "receipt_account",
    "FundRepayAccountService": "repay_account",
    # Financial system
    "FinancialSystemReceiptService": "financial_receipt",
    "FinancialSystemRepaymentService": "financial_repayment",
    "FinancialSystemService": "financial_system",
    "FinancialSystemContractService": "financial_contract",
    "FundFinancialSystemService": "fund_financial_system",
    # Other fund services
    "FundInitService": "fund_init",
}

# ============================================================
# Method call mappings: (category, method) -> facade_method
# ============================================================
# These map (service_category, original_method_name) -> fundFacade.newMethodName
# The key is (category_pattern, method_name) where category_pattern can match multiple categories

METHOD_MAPPINGS = {
    # getById
    ("financing", "getById"): "fundFacade.getFinancingById",
    ("direct_financing", "getById"): "fundFacade.getDirectFinancingById",
    ("credit", "getById"): "fundFacade.getCreditById",
    ("receipt_repay", "getById"): "fundFacade.getReceiptRepayById",
    ("guarantee_agency", "getById"): "fundFacade.getGuaranteeAgencyById",

    # listByIds
    ("financing", "listByIds"): "fundFacade.listFinancingByIds",
    ("direct_financing", "listByIds"): "fundFacade.listDirectFinancingByIds",
    ("credit", "listByIds"): "fundFacade.listCreditsByIds",
    ("receipt_repay", "listByIds"): "fundFacade.listReceiptRepayByIds",
    ("guarantee_agency", "listByIds"): "fundFacade.getGuaranteeAgenciesByIds",

    # list (LambdaQueryWrapper)
    ("financing", "list"): "fundFacade.listFinancing",
    ("direct_financing", "list"): "fundFacade.listDirectFinancing",
    ("credit", "list"): "fundFacade.listCredits",
    ("receipt_repay", "list"): "fundFacade.listReceiptRepay",
    ("financing_pledge", "list"): "fundFacade.listPledgeInfo",
    ("direct_financing_pledge", "list"): "fundFacade.listDirectPledgeInfo",
    ("direct_financing_product", "list"): "fundFacade.listDirectProductDetail",
    ("receipt_repay_cashflow", "list"): "fundFacade.listCashFlow",
    ("receipt_flow_detail", "list"): "fundFacade.listReceiptFlowDetail",
    ("financing_plan", "list"): "fundFacade.listFinancingPlan",
    ("direct_financing_repay_actual", "list"): "fundFacade.listDirectRepayActual",
    ("financing_repay_actual", "list"): "fundFacade.listFinancingRepayActual",
    ("organization", "list"): "fundFacade.listOrganizations",

    # count
    ("financing", "count"): "fundFacade.countFinancing",

    # getOne
    ("organization", "getOne"): "fundFacade.getOneOrganization",
    ("guarantee_agency", "getOne"): "fundFacade.getOneGuaranteeAgency",
    ("guarantee_info", "getOne"): "fundFacade.getOneGuaranteeInfo",
    ("receipt_repay", "getOne"): "fundFacade.getOneReceiptRepay",

    # Organization-specific
    ("organization", "getByFinancingId"): "fundFacade.getOrganizationsByFinancingId",
    ("organization", "getNamesByIds"): "fundFacade.getOrganizationNamesByIds",
    ("organization", "getBatchByFinancingId"): "fundFacade.getOrganizationsBatchByFinancingIds",
    ("organization", "listByIds"): "fundFacade.getOrganizationsByIds",

    # Credit ref
    ("credit_ref", "queryByFinancingId"): "fundFacade.getCreditRefsByFinancingId",

    # Pledge
    ("financing_pledge", "findContractPledgeList"): "fundFacade.findContractPledgeList",
    ("financing_pledge", "getMapByFinancings"): "fundFacade.getPledgeMapByFinancings",

    # Receipt/repay specific
    ("receipt_repay", "queryRemainingAmount"): "fundFacade.queryRemainingAmount",
    ("receipt_flow_detail", "listByCashFlowCodes"): "fundFacade.listReceiptFlowDetailByCashFlowCodes",

    # Direct financing specific
    ("direct_financing", "updateFinancingCost"): "fundFacade.updateDirectFinancingCost",
    ("direct_financing", "syncToFlowPlan"): "fundFacade.syncDirectFinancingToFlowPlan",

    # Receipt repay sync
    ("receipt_repay", "syncToFlowPlan"): "fundFacade.syncReceiptRepayToFlowPlan",

    # Financing specific
    ("financing", "tryUpdateChangeOther"): "fundFacade.tryUpdateChangeOther",
    ("financing", "updateHasPledge"): "fundFacade.updateFinancingHasPledge",

    # Guarantee
    ("guarantee_agency", "getUsedGuaranteeLimit"): "fundFacade.getUsedGuaranteeLimit",
    ("guarantee_info", "remainingGuaranteeLimit"): "fundFacade.remainingGuaranteeLimit",

    # Direct financing receipt
    ("receipt_repay", "addDirectFinancingReceipt"): "fundFacade.addDirectFinancingReceipt",
    ("receipt_repay", "upgradeDirectFinancingReceipt"): "fundFacade.upgradeDirectFinancingReceipt",

    # Direct financing product
    ("direct_financing_product", "listByFinancingId"): "fundFacade.listDirectProductDetail",  # approximate
}


def discover_files(base_dir):
    """Find all Java files with fund service imports."""
    result = []
    src_dir = os.path.join(base_dir, "src")
    for root, dirs, files in os.walk(src_dir):
        for f in files:
            if f.endswith(".java"):
                path = os.path.join(root, f)
                with open(path, "r", encoding="utf-8") as fh:
                    content = fh.read()
                for prefix in FUND_SERVICE_IMPORT_PREFIXES:
                    if prefix in content:
                        result.append(path)
                        break
    return result


def find_fund_service_imports(content):
    """Return list of (full_import_line, service_class_name) for fund service imports."""
    results = []
    for line in content.split("\n"):
        stripped = line.strip()
        for prefix in FUND_SERVICE_IMPORT_PREFIXES:
            if stripped.startswith(prefix):
                # Extract class name: last segment before ;
                # Handle wildcard imports too
                if stripped.endswith(".*;"):
                    results.append((stripped, None))  # wildcard
                else:
                    class_name = stripped.rstrip(";").rsplit(".", 1)[-1]
                    results.append((stripped, class_name))
    return results


def find_field_declarations(content, service_classes):
    """
    Find @Resource/@Autowired field declarations for fund services.
    Returns list of (field_name, service_class_name, full_match_text_to_remove).
    """
    fields = []
    # Pattern: optional annotation line(s) + field declaration
    # e.g.:
    #     @Resource
    #     private FundFinancingBaseInfoService financingBaseInfoService;
    for svc_class in service_classes:
        # Match the field declaration with preceding @Resource or @Autowired
        pattern = re.compile(
            r'(\s*@(?:Resource|Autowired)(?:\([^)]*\))?\s*\n)'
            r'(\s*private\s+' + re.escape(svc_class) + r'\s+(\w+)\s*;)',
            re.MULTILINE
        )
        for m in pattern.finditer(content):
            annotation_line = m.group(1)
            field_line = m.group(2)
            field_name = m.group(3)
            full_text = m.group(0)
            fields.append((field_name, svc_class, full_text))

    return fields


def find_field_declarations_no_annotation(content, service_classes):
    """Find field declarations without @Resource/@Autowired (rare but possible)."""
    fields = []
    for svc_class in service_classes:
        pattern = re.compile(
            r'\s*private\s+' + re.escape(svc_class) + r'\s+(\w+)\s*;'
        )
        for m in pattern.finditer(content):
            field_name = m.group(1)
            fields.append((field_name, svc_class, m.group(0)))
    return fields


def get_category_for_field(service_class_name, field_name):
    """Determine the category for a field based on service class name."""
    if service_class_name in SERVICE_CATEGORIES:
        return SERVICE_CATEGORIES[service_class_name]
    return None


def replace_method_calls(content, field_name, category):
    """Replace field_name.method( calls with fundFacade.newMethod( calls."""
    changes = 0
    for (cat, method), facade_call in METHOD_MAPPINGS.items():
        if cat == category:
            # Replace fieldName.method( with fundFacade.newMethod(
            old_pattern = re.escape(field_name) + r'\.' + re.escape(method) + r'\('
            new_text = facade_call + '('
            new_content = re.sub(old_pattern, new_text, content)
            if new_content != content:
                changes += content.count(field_name + '.' + method + '(')
                content = new_content
    return content, changes


def replace_spring_util_getbean(content, service_classes):
    """Replace SpringUtil.getBean(FundXxxService.class) patterns."""
    changes = 0
    for svc_class in service_classes:
        if svc_class is None:
            continue
        # SpringUtil.getBean(FundXxxService.class).method(
        # -> fundFacade.facadeMethod(  (if we can determine the method)
        # But since the call chain includes .method(), we need a different approach
        # For now, replace getBean(Service.class) with getBean(FundFacade.class)
        for getter in ["SpringUtil.getBean", "SpringContextHolder.getBean"]:
            old = f"{getter}({svc_class}.class)"
            new = f"{getter}(FundFacade.class)"
            if old in content:
                count = content.count(old)
                content = content.replace(old, new)
                changes += count
    return content, changes


def replace_spring_getbean_method_calls(content, service_classes):
    """
    After replacing getBean(Service.class) with getBean(FundFacade.class),
    we need to also rewrite the chained method calls.
    e.g. SpringUtil.getBean(FundFacade.class).getById(id)
      -> fundFacade.getDirectFinancingById(id)  (depends on original service)

    This is complex because we've already lost which service it was.
    Strategy: do the method rewriting BEFORE the getBean class replacement.
    """
    changes = 0
    for svc_class in service_classes:
        if svc_class is None:
            continue
        category = SERVICE_CATEGORIES.get(svc_class)
        if not category:
            continue

        for getter in ["SpringUtil.getBean", "SpringContextHolder.getBean"]:
            for (cat, method), facade_call in METHOD_MAPPINGS.items():
                if cat == category:
                    old = f"{getter}({svc_class}.class).{method}("
                    new = f"{facade_call}("
                    if old in content:
                        count = content.count(old)
                        content = content.replace(old, new)
                        changes += count
    return content, changes


def has_fund_facade_field(content):
    """Check if content already has a fundFacade field."""
    return bool(re.search(r'private\s+FundFacade\s+fundFacade\s*;', content))


def has_fund_facade_import(content):
    """Check if content already has the FundFacade import."""
    return FUND_FACADE_IMPORT in content


def add_fund_facade_import(content):
    """Add FundFacade import after the last existing import."""
    if has_fund_facade_import(content):
        return content

    lines = content.split("\n")
    last_import_idx = -1
    for i, line in enumerate(lines):
        if line.strip().startswith("import "):
            last_import_idx = i

    if last_import_idx >= 0:
        lines.insert(last_import_idx + 1, FUND_FACADE_IMPORT)
    return "\n".join(lines)


def add_fund_facade_field(content):
    """Add @Resource private FundFacade fundFacade; field."""
    if has_fund_facade_field(content):
        return content

    # Strategy 1: Add after the first remaining @Resource field
    pattern = re.compile(r'(\s*@Resource\s*\n\s*private\s+\w+\s+\w+\s*;)', re.MULTILINE)
    m = pattern.search(content)
    if m:
        insert_after = m.end()
        indent = "    "  # typical Java indent
        # Detect actual indent from the matched field
        field_line = m.group(0)
        indent_match = re.match(r'(\s*)', field_line)
        if indent_match:
            indent = indent_match.group(1)
        facade_field = f"\n{indent}@Resource\n{indent}private FundFacade fundFacade;"
        content = content[:insert_after] + facade_field + content[insert_after:]
        return content

    # Strategy 2: Add after @Autowired field
    pattern = re.compile(r'(\s*@Autowired\s*\n\s*private\s+\w+\s+\w+\s*;)', re.MULTILINE)
    m = pattern.search(content)
    if m:
        insert_after = m.end()
        indent = "    "
        field_line = m.group(0)
        indent_match = re.match(r'(\s*)', field_line)
        if indent_match:
            indent = indent_match.group(1)
        facade_field = f"\n{indent}@Resource\n{indent}private FundFacade fundFacade;"
        content = content[:insert_after] + facade_field + content[insert_after:]
        return content

    # Strategy 3: Add after class declaration line
    pattern = re.compile(r'(public\s+class\s+\w+[^{]*\{)', re.MULTILINE)
    m = pattern.search(content)
    if m:
        insert_after = m.end()
        facade_field = "\n\n    @Resource\n    private FundFacade fundFacade;"
        content = content[:insert_after] + facade_field + content[insert_after:]
        return content

    return content


def ensure_resource_import(content):
    """Ensure javax.annotation.Resource import exists."""
    if "import javax.annotation.Resource;" in content:
        return content
    # Add it near other javax imports or after last import
    lines = content.split("\n")
    last_import_idx = -1
    for i, line in enumerate(lines):
        if line.strip().startswith("import "):
            last_import_idx = i
    if last_import_idx >= 0:
        lines.insert(last_import_idx + 1, "import javax.annotation.Resource;")
    return "\n".join(lines)


def migrate_file(filepath, dry_run=False):
    """
    Migrate a single file. Returns (success, changes_description).
    """
    with open(filepath, "r", encoding="utf-8") as f:
        original_content = f.read()

    content = original_content
    changes = []
    total_method_changes = 0

    # Step 1: Find fund service imports
    fund_imports = find_fund_service_imports(content)
    if not fund_imports:
        return False, "No fund service imports found"

    service_classes = [cls for _, cls in fund_imports if cls is not None]

    # Step 2: Before modifying getBean calls, rewrite chained method calls
    # e.g. SpringUtil.getBean(FundXxxService.class).method(args) -> fundFacade.facadeMethod(args)
    content, getbean_method_changes = replace_spring_getbean_method_calls(content, service_classes)
    if getbean_method_changes:
        changes.append(f"  Rewrote {getbean_method_changes} SpringUtil/SpringContextHolder.getBean().method() chains")
        total_method_changes += getbean_method_changes

    # Step 3: Replace remaining SpringUtil.getBean(Service.class) with getBean(FundFacade.class)
    content, getbean_changes = replace_spring_util_getbean(content, service_classes)
    if getbean_changes:
        changes.append(f"  Replaced {getbean_changes} SpringUtil/SpringContextHolder.getBean() class refs")

    # Step 4: Find and collect field declarations (before removing them)
    field_decls = find_field_declarations(content, service_classes)
    field_decls_no_ann = find_field_declarations_no_annotation(content, service_classes)

    # Merge (avoid duplicates - no_ann might overlap with annotated ones)
    all_field_names = set()
    field_info = []  # (field_name, service_class, text_to_remove)
    for fn, sc, txt in field_decls:
        if fn not in all_field_names:
            all_field_names.add(fn)
            field_info.append((fn, sc, txt))
    for fn, sc, txt in field_decls_no_ann:
        if fn not in all_field_names:
            all_field_names.add(fn)
            field_info.append((fn, sc, txt))

    # Step 5: Replace method calls for each field
    for field_name, svc_class, _ in field_info:
        category = get_category_for_field(svc_class, field_name)
        if category:
            content, mc = replace_method_calls(content, field_name, category)
            if mc:
                total_method_changes += mc
                changes.append(f"  Replaced {mc} method calls: {field_name}.* -> fundFacade.*")

    # Step 6: Remove field declarations
    for _, _, text_to_remove in field_info:
        if text_to_remove in content:
            content = content.replace(text_to_remove, "", 1)
            # Clean up any resulting blank lines
    if field_info:
        changes.append(f"  Removed {len(field_info)} field declarations")

    # Step 7: Remove fund service imports
    removed_imports = 0
    for import_line, _ in fund_imports:
        if import_line in content:
            content = content.replace(import_line + "\n", "", 1)
            if import_line in content:  # might not have trailing newline
                content = content.replace(import_line, "", 1)
            removed_imports += 1
    if removed_imports:
        changes.append(f"  Removed {removed_imports} fund service imports")

    # Step 8: Add FundFacade import
    if not has_fund_facade_import(content):
        content = add_fund_facade_import(content)
        changes.append("  Added FundFacade import")

    # Step 9: Add fundFacade field (if needed and if we removed at least one field)
    if field_info and not has_fund_facade_field(content):
        content = ensure_resource_import(content)
        content = add_fund_facade_field(content)
        changes.append("  Added @Resource FundFacade fundFacade field")

    # Step 10: Clean up multiple blank lines
    content = re.sub(r'\n{3,}', '\n\n', content)

    if content == original_content:
        return False, "No changes needed"

    if not dry_run:
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(content)

    summary = f"Migrated ({len(field_info)} fields, {total_method_changes} method calls)"
    return True, summary + "\n" + "\n".join(changes)


def main():
    base_dir = os.path.dirname(os.path.abspath(__file__))

    if len(sys.argv) > 1 and sys.argv[1] == "--all":
        files = discover_files(base_dir)
        print(f"Discovered {len(files)} files with fund service imports")
    elif len(sys.argv) > 1 and sys.argv[1] == "--dry-run":
        files = discover_files(base_dir)
        print(f"[DRY RUN] Discovered {len(files)} files with fund service imports")
        for filepath in sorted(files):
            rel = os.path.relpath(filepath, base_dir)
            success, desc = migrate_file(filepath, dry_run=True)
            status = "WOULD MIGRATE" if success else "SKIP"
            print(f"  [{status}] {rel}: {desc.split(chr(10))[0]}")
        return
    elif len(sys.argv) > 1:
        files = sys.argv[1:]
    else:
        print("Usage:")
        print("  python3 batch-migrate.py --all        # migrate all discovered files")
        print("  python3 batch-migrate.py --dry-run    # dry run on all discovered files")
        print("  python3 batch-migrate.py file1 file2  # migrate specific files")
        sys.exit(1)

    success_count = 0
    fail_count = 0
    skip_count = 0

    for filepath in sorted(files):
        rel = os.path.relpath(filepath, base_dir)
        if not os.path.exists(filepath):
            print(f"  [NOT FOUND] {filepath}")
            fail_count += 1
            continue

        success, desc = migrate_file(filepath)
        if success:
            success_count += 1
            print(f"  [OK] {rel}")
            for line in desc.split("\n"):
                if line.strip():
                    print(f"       {line.strip()}")
        else:
            skip_count += 1
            print(f"  [SKIP] {rel}: {desc}")

    print(f"\n{'='*60}")
    print(f"Summary: {success_count} migrated, {skip_count} skipped, {fail_count} failed")
    print(f"Total files processed: {len(files)}")


if __name__ == "__main__":
    main()

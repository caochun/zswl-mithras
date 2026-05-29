#!/usr/bin/env python3
"""
Migrate external code from direct fund service injection to FundFacade.
Only handles files with 1-2 fund service imports (safest).
"""
import re, os, sys

SVC = "src/main/java/cn/zswltech/mithras/service"
FACADE_IMPORT = "import cn.zswltech.mithras.service.facade.fund.FundFacade;"

# Map: old service class -> (field name pattern, {old_method: new_facade_method})
SERVICE_MAP = {
    "FundOrganizationService": {
        "field_patterns": ["fundOrganizationService", "organizationService"],
        "methods": {
            "getByFinancingId": "getOrganizationsByFinancingId",
            "listByIds": "getOrganizationsByIds",
            "getNamesByIds": "getOrganizationNamesByIds",
            "getBatchByFinancingId": "getOrganizationsBatchByFinancingIds",
        }
    },
    "FundFinancingBaseInfoService": {
        "field_patterns": ["fundFinancingBaseInfoService", "financingBaseInfoService"],
        "methods": {
            "getById": "getFinancingById",
            "listByIds": "listFinancingByIds",
            "list": "listFinancing",
            "count": "countFinancing",
        }
    },
    "FundDirectFinancingBaseInfoService": {
        "field_patterns": ["fundDirectFinancingBaseInfoService", "directFinancingBaseInfoService"],
        "methods": {
            "getById": "getDirectFinancingById",
            "listByIds": "listDirectFinancingByIds",
            "list": "listDirectFinancing",
        }
    },
    "FundCreditService": {
        "field_patterns": ["fundCreditService", "creditService"],
        "methods": {
            "getById": "getCreditById",
        }
    },
    "FundReceiptRepayBaseInfoService": {
        "field_patterns": ["fundReceiptRepayBaseInfoService", "receiptRepayBaseInfoService"],
        "methods": {
            "getById": "getReceiptRepayById",
            "list": "listReceiptRepay",
            "listByIds": "listReceiptRepayByIds",
        }
    },
    "FundFinancingCreditRefService": {
        "field_patterns": ["fundFinancingCreditRefService"],
        "methods": {}
    },
    "FundFinancingPledgeInfoService": {
        "field_patterns": ["fundFinancingPledgeInfoService"],
        "methods": {
            "list": "listPledgeInfo",
            "findContractPledgeList": "findContractPledgeList",
        }
    },
}

def migrate_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()
    original = content

    # Find which fund services are imported
    fund_imports = re.findall(
        r'import cn\.zswltech\.mithras\.service\.(?:service\.fund|fund\.direct\.service)\.(?:\w+\.)*(\w+Service\w*);',
        content
    )

    if not fund_imports:
        return False, "no fund imports"

    migrated_services = []
    for svc_class in fund_imports:
        if svc_class not in SERVICE_MAP:
            continue

        info = SERVICE_MAP[svc_class]

        # Remove old import
        content = re.sub(
            r'import cn\.zswltech\.mithras\.service\.(?:service\.fund|fund\.direct\.service)\.(?:\w+\.)*' + svc_class + r';\n',
            '', content
        )

        # Replace field declaration
        for field_pat in info["field_patterns"]:
            # @Resource\n    private XxxService fieldName;
            content = re.sub(
                r'(\s*@Resource\s*\n\s*private\s+)' + svc_class + r'\s+' + field_pat + r'\s*;',
                '', content
            )
            # Replace method calls
            for old_method, new_method in info["methods"].items():
                content = content.replace(f'{field_pat}.{old_method}(', f'fundFacade.{new_method}(')

        migrated_services.append(svc_class)

    if not migrated_services:
        return False, "no migratable services"

    # Add FundFacade import if not present
    if FACADE_IMPORT not in content:
        content = re.sub(
            r'(package [^;]+;\n)',
            r'\1\n' + FACADE_IMPORT + '\n',
            content
        )

    # Add FundFacade field if not present
    if 'private FundFacade fundFacade;' not in content:
        # Add after the last @Resource field
        content = re.sub(
            r'((?:@Resource\s*\n\s*private\s+\w+\s+\w+;\s*\n)+)',
            r'\1    @Resource\n    private FundFacade fundFacade;\n',
            content, count=1
        )

    if content == original:
        return False, "no changes made"

    with open(filepath, 'w') as f:
        f.write(content)
    return True, f"migrated: {', '.join(migrated_services)}"


def main():
    migrated = 0
    skipped = 0
    failed = 0

    # Find all external files importing fund services
    import subprocess
    result = subprocess.run(
        f'grep -rl "import.*service\\.service\\.fund\\.\\|import.*service\\.fund\\.direct\\.service\\." '
        f'{SVC} --include="*.java" | grep -v "/fund/" | grep -v "/facade/"',
        shell=True, capture_output=True, text=True
    )

    files = [f.strip() for f in result.stdout.strip().split('\n') if f.strip()]

    for filepath in files:
        rel = filepath.replace(SVC + '/', '')

        # Count fund service imports
        with open(filepath) as f:
            fund_count = len(re.findall(
                r'import cn\.zswltech\.mithras\.service\.(?:service\.fund|fund\.direct\.service)\.',
                f.read()
            ))

        # Only migrate files with 1-2 fund imports (safest)
        if fund_count > 2:
            print(f"[SKIP] {rel} ({fund_count} fund imports)")
            skipped += 1
            continue

        ok, msg = migrate_file(filepath)
        if ok:
            print(f"[OK]   {rel} - {msg}")
            migrated += 1
        else:
            print(f"[FAIL] {rel} - {msg}")
            failed += 1

    print(f"\nMigrated: {migrated}, Skipped: {skipped}, Failed: {failed}")


if __name__ == '__main__':
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    main()

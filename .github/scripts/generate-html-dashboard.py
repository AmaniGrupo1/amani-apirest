#!/usr/bin/env python3
"""
Genera un dashboard HTML profesional a partir de reportes JUnit XML.
Soporta Surefire y Failsafe reports.
"""

import os
import sys
import xml.etree.ElementTree as ET
from pathlib import Path
from datetime import datetime
import html

HTML_TEMPLATE = """<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Test Dashboard - {project_name}</title>
    <style>
        :root {{
            --bg: #0d1117;
            --surface: #161b22;
            --border: #30363d;
            --text: #c9d1d9;
            --text-secondary: #8b949e;
            --success: #238636;
            --success-bg: rgba(35, 134, 54, 0.15);
            --danger: #da3633;
            --danger-bg: rgba(218, 54, 51, 0.15);
            --warning: #9e6a03;
            --warning-bg: rgba(158, 106, 3, 0.15);
            --info: #1f6feb;
            --info-bg: rgba(31, 111, 235, 0.15);
        }}
        * {{ margin: 0; padding: 0; box-sizing: border-box; }}
        body {{
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: var(--bg);
            color: var(--text);
            line-height: 1.6;
        }}
        .container {{ max-width: 1400px; margin: 0 auto; padding: 2rem; }}
        header {{
            border-bottom: 1px solid var(--border);
            padding-bottom: 1.5rem;
            margin-bottom: 2rem;
        }}
        h1 {{ font-size: 1.75rem; margin-bottom: 0.5rem; }}
        .meta {{ color: var(--text-secondary); font-size: 0.875rem; }}
        .grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin-bottom: 2rem;
        }}
        .card {{
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: 0.75rem;
            padding: 1.25rem;
            text-align: center;
        }}
        .card .icon {{ font-size: 1.5rem; margin-bottom: 0.5rem; }}
        .card .value {{ font-size: 2rem; font-weight: 700; }}
        .card .label {{ color: var(--text-secondary); font-size: 0.875rem; margin-top: 0.25rem; }}
        .card.success {{ border-top: 3px solid var(--success); }}
        .card.danger {{ border-top: 3px solid var(--danger); }}
        .card.warning {{ border-top: 3px solid var(--warning); }}
        .card.info {{ border-top: 3px solid var(--info); }}
        .section {{
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: 0.75rem;
            margin-bottom: 1.5rem;
            overflow: hidden;
        }}
        .section-header {{
            padding: 1rem 1.25rem;
            border-bottom: 1px solid var(--border);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }}
        .section-header h2 {{ font-size: 1.125rem; font-weight: 600; }}
        .section-body {{ padding: 1.25rem; }}
        table {{
            width: 100%;
            border-collapse: collapse;
            font-size: 0.875rem;
        }}
        th, td {{ padding: 0.75rem 1rem; text-align: left; border-bottom: 1px solid var(--border); }}
        th {{ color: var(--text-secondary); font-weight: 500; text-transform: uppercase; font-size: 0.75rem; letter-spacing: 0.05em; }}
        tr:hover {{ background: rgba(255,255,255,0.03); }}
        .badge {{
            display: inline-flex;
            align-items: center;
            gap: 0.25rem;
            padding: 0.25rem 0.5rem;
            border-radius: 0.375rem;
            font-size: 0.75rem;
            font-weight: 600;
        }}
        .badge.success {{ background: var(--success-bg); color: #3fb950; }}
        .badge.danger {{ background: var(--danger-bg); color: #f85149; }}
        .badge.warning {{ background: var(--warning-bg); color: #d29922; }}
        .badge.info {{ background: var(--info-bg); color: #58a6ff; }}
        .progress-bar {{
            width: 100%;
            height: 0.5rem;
            background: var(--border);
            border-radius: 0.25rem;
            overflow: hidden;
            margin-top: 0.5rem;
        }}
        .progress-bar .fill {{
            height: 100%;
            border-radius: 0.25rem;
            transition: width 0.5s ease;
        }}
        .fill.success {{ background: var(--success); }}
        .fill.danger {{ background: var(--danger); }}
        .fill.warning {{ background: var(--warning); }}
        .test-case {{
            padding: 0.75rem;
            border-radius: 0.5rem;
            margin-bottom: 0.5rem;
            border-left: 3px solid transparent;
        }}
        .test-case.pass {{ background: var(--success-bg); border-left-color: var(--success); }}
        .test-case.fail {{ background: var(--danger-bg); border-left-color: var(--danger); }}
        .test-case.skip {{ background: var(--warning-bg); border-left-color: var(--warning); }}
        .test-case .name {{ font-weight: 600; font-size: 0.875rem; }}
        .test-case .time {{ color: var(--text-secondary); font-size: 0.75rem; margin-top: 0.25rem; }}
        .test-case .error {{
            margin-top: 0.5rem;
            padding: 0.5rem;
            background: rgba(0,0,0,0.3);
            border-radius: 0.25rem;
            font-family: monospace;
            font-size: 0.75rem;
            color: #f85149;
            white-space: pre-wrap;
            word-break: break-word;
            max-height: 200px;
            overflow-y: auto;
        }}
        .suite-header {{
            display: flex;
            align-items: center;
            gap: 0.75rem;
            padding: 0.75rem 1rem;
            background: rgba(255,255,255,0.02);
            border-bottom: 1px solid var(--border);
            cursor: pointer;
        }}
        .suite-header:hover {{ background: rgba(255,255,255,0.04); }}
        .suite-header .toggle {{ color: var(--text-secondary); font-size: 0.75rem; }}
        .suite-content {{ display: none; }}
        .suite-content.open {{ display: block; }}
        .empty-state {{
            text-align: center;
            padding: 3rem;
            color: var(--text-secondary);
        }}
        .empty-state .icon {{ font-size: 3rem; margin-bottom: 1rem; opacity: 0.5; }}
        @media (max-width: 768px) {{
            .container {{ padding: 1rem; }}
            .grid {{ grid-template-columns: repeat(2, 1fr); }}
        }}
    </style>
</head>
<body>
    <div class="container">
        <header>
            <h1>📊 Test Dashboard</h1>
            <p class="meta">{project_name} &middot; Generado el {timestamp}</p>
        </header>

        <div class="grid">
            <div class="card info">
                <div class="icon">🧪</div>
                <div class="value">{total}</div>
                <div class="label">Tests Totales</div>
            </div>
            <div class="card success">
                <div class="icon">✅</div>
                <div class="value">{passed}</div>
                <div class="label">Pasados</div>
            </div>
            <div class="card danger">
                <div class="icon">❌</div>
                <div class="value">{failed}</div>
                <div class="label">Fallidos</div>
            </div>
            <div class="card danger">
                <div class="icon">💥</div>
                <div class="value">{errors}</div>
                <div class="label">Errores</div>
            </div>
            <div class="card warning">
                <div class="icon">⏭️</div>
                <div class="value">{skipped}</div>
                <div class="label">Skipped</div>
            </div>
            <div class="card info">
                <div class="icon">⏱️</div>
                <div class="value">{time:.2f}s</div>
                <div class="label">Tiempo Total</div>
            </div>
        </div>

        <div class="section">
            <div class="section-header">
                <h2>📈 Resumen de Ejecución</h2>
                <span class="badge {status_class}">{status_text}</span>
            </div>
            <div class="section-body">
                <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 0.5rem;">
                    <span style="font-size: 0.875rem; color: var(--text-secondary); min-width: 80px;">Exito</span>
                    <div class="progress-bar" style="flex: 1;">
                        <div class="fill success" style="width: {pass_pct}%;"></div>
                    </div>
                    <span style="font-size: 0.875rem; font-weight: 600; min-width: 50px; text-align: right;">{pass_pct:.1f}%</span>
                </div>
                <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 0.5rem;">
                    <span style="font-size: 0.875rem; color: var(--text-secondary); min-width: 80px;">Fallo</span>
                    <div class="progress-bar" style="flex: 1;">
                        <div class="fill danger" style="width: {fail_pct}%;"></div>
                    </div>
                    <span style="font-size: 0.875rem; font-weight: 600; min-width: 50px; text-align: right;">{fail_pct:.1f}%</span>
                </div>
                <div style="display: flex; align-items: center; gap: 1rem;">
                    <span style="font-size: 0.875rem; color: var(--text-secondary); min-width: 80px;">Skip</span>
                    <div class="progress-bar" style="flex: 1;">
                        <div class="fill warning" style="width: {skip_pct}%;"></div>
                    </div>
                    <span style="font-size: 0.875rem; font-weight: 600; min-width: 50px; text-align: right;">{skip_pct:.1f}%</span>
                </div>
            </div>
        </div>

        {failed_section}

        <div class="section">
            <div class="section-header">
                <h2>📋 Suites de Tests</h2>
                <span class="badge info">{suite_count} suites</span>
            </div>
            <div class="section-body" style="padding: 0;">
                {suites_html}
            </div>
        </div>
    </div>

    <script>
        document.querySelectorAll('.suite-header').forEach(header => {{
            header.addEventListener('click', () => {{
                const content = header.nextElementSibling;
                const toggle = header.querySelector('.toggle');
                content.classList.toggle('open');
                toggle.textContent = content.classList.contains('open') ? '▼' : '▶';
            }});
        }});
    </script>
</body>
</html>
"""


def parse_junit_xml(xml_path):
    """Parsea un archivo JUnit XML y retorna datos estructurados."""
    tree = ET.parse(xml_path)
    root = tree.getroot()

    # Manejar posible namespace
    ns = {'junit': root.tag.split('}')[0].strip('{')} if '}' in root.tag else {}

    suite_name = root.get('name', Path(xml_path).stem.replace('TEST-', ''))
    tests = int(root.get('tests', 0))
    failures = int(root.get('failures', 0))
    errors = int(root.get('errors', 0))
    skipped = int(root.get('skipped', 0))
    time = float(root.get('time', 0))

    test_cases = []

    # Buscar testcases considerando namespace
    testcase_xpath = './/junit:testcase' if ns else './/testcase'
    for tc in root.iterfind(testcase_xpath, ns) if ns else root.iter('testcase'):
        name = tc.get('name', 'Unknown')
        classname = tc.get('classname', '')
        tc_time = float(tc.get('time', 0))

        status = 'pass'
        error_msg = ''

        # Verificar fallos/errores/skips
        failure_xpath = 'junit:failure' if ns else 'failure'
        error_xpath = 'junit:error' if ns else 'error'
        skip_xpath = 'junit:skipped' if ns else 'skipped'

        if tc.find(failure_xpath, ns) is not None or tc.find('failure') is not None:
            status = 'fail'
            fail_elem = tc.find(failure_xpath, ns)
            if fail_elem is None:
                fail_elem = tc.find('failure')
            if fail_elem is not None:
                error_msg = fail_elem.get('message', '') or fail_elem.text or ''
        elif tc.find(error_xpath, ns) is not None or tc.find('error') is not None:
            status = 'fail'
            err_elem = tc.find(error_xpath, ns)
            if err_elem is None:
                err_elem = tc.find('error')
            if err_elem is not None:
                error_msg = err_elem.get('message', '') or err_elem.text or ''
        elif tc.find(skip_xpath, ns) is not None or tc.find('skipped') is not None:
            status = 'skip'

        test_cases.append({
            'name': name,
            'classname': classname,
            'time': tc_time,
            'status': status,
            'error': error_msg.strip()
        })

    return {
        'name': suite_name,
        'tests': tests,
        'failures': failures,
        'errors': errors,
        'skipped': skipped,
        'time': time,
        'test_cases': test_cases,
        'file': Path(xml_path).name
    }


def generate_dashboard(xml_dirs, output_dir):
    """Genera el dashboard HTML a partir de directorios con XML JUnit."""

    all_suites = []
    total_tests = 0
    total_failures = 0
    total_errors = 0
    total_skipped = 0
    total_time = 0.0

    # Buscar todos los XML JUnit
    for xml_dir in xml_dirs:
        if not Path(xml_dir).exists():
            continue
        for xml_file in Path(xml_dir).glob('TEST-*.xml'):
            try:
                suite = parse_junit_xml(xml_file)
                all_suites.append(suite)
                total_tests += suite['tests']
                total_failures += suite['failures']
                total_errors += suite['errors']
                total_skipped += suite['skipped']
                total_time += suite['time']
            except Exception as e:
                print(f"Error parseando {xml_file}: {e}", file=sys.stderr)

    # Ordenar suites: fallidos primero, luego por nombre
    all_suites.sort(key=lambda s: (s['failures'] + s['errors'] == 0, s['name']))

    # Generar HTML de suites
    suites_html = ""
    for suite in all_suites:
        status_class = "success" if suite['failures'] + suite['errors'] == 0 else "danger"
        status_text = "PASS" if suite['failures'] + suite['errors'] == 0 else f"FAIL ({suite['failures'] + suite['errors']})"

        test_cases_html = ""
        for tc in suite['test_cases']:
            status = tc['status']
            error_html = f'<div class="error">{html.escape(tc["error"][:500])}</div>' if tc['error'] else ''
            test_cases_html += f'''
                <div class="test-case {status}">
                    <div class="name">{html.escape(tc["name"])}</div>
                    <div class="time">{tc["time"]:.3f}s &middot; {html.escape(tc["classname"])}</div>
                    {error_html}
                </div>
            '''

        if not suite['test_cases']:
            test_cases_html = '<div class="empty-state"><div class="icon">📭</div><p>No hay casos de test en este suite</p></div>'

        suites_html += f'''
            <div class="suite-header">
                <span class="badge {status_class}">{status_text}</span>
                <span style="flex: 1; font-weight: 500; font-size: 0.875rem;">{html.escape(suite["name"])}</span>
                <span style="color: var(--text-secondary); font-size: 0.75rem;">{suite["tests"]} tests &middot; {suite["time"]:.2f}s</span>
                <span class="toggle">▶</span>
            </div>
            <div class="suite-content">
                <div style="padding: 1rem;">
                    {test_cases_html}
                </div>
            </div>
        '''

    if not all_suites:
        suites_html = '<div class="empty-state"><div class="icon">📭</div><p>No se encontraron reportes JUnit</p></div>'

    # Sección de tests fallidos
    failed_tests = []
    for suite in all_suites:
        for tc in suite['test_cases']:
            if tc['status'] == 'fail':
                failed_tests.append({
                    'suite': suite['name'],
                    **tc
                })

    if failed_tests:
        failed_html = ""
        for ft in failed_tests:
            error_html = f'<div class="error">{html.escape(ft["error"][:500])}</div>' if ft['error'] else ''
            failed_html += f'''
                <div class="test-case fail">
                    <div class="name">{html.escape(ft["name"])}</div>
                    <div class="time">{ft["time"]:.3f}s &middot; {html.escape(ft["classname"])} &middot; {html.escape(ft["suite"])}</div>
                    {error_html}
                </div>
            '''

        failed_section = f'''
        <div class="section">
            <div class="section-header">
                <h2>🔴 Tests Fallidos ({len(failed_tests)})</h2>
                <span class="badge danger">CRITICO</span>
            </div>
            <div class="section-body">
                {failed_html}
            </div>
        </div>
        '''
    else:
        failed_section = ''

    # Calcular porcentajes
    total_ran = total_tests - total_skipped
    pass_pct = (100.0 * (total_ran - total_failures - total_errors) / total_ran) if total_ran > 0 else 0
    fail_pct = (100.0 * (total_failures + total_errors) / total_tests) if total_tests > 0 else 0
    skip_pct = (100.0 * total_skipped / total_tests) if total_tests > 0 else 0

    # Estado global
    if total_failures + total_errors == 0:
        status_class = "success"
        status_text = "BUILD EXITOSO"
    else:
        status_class = "danger"
        status_text = "BUILD FALLIDO"

    # Generar HTML final
    html_content = HTML_TEMPLATE.format(
        project_name="Amani API REST",
        timestamp=datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
        total=total_tests,
        passed=total_ran - total_failures - total_errors,
        failed=total_failures,
        errors=total_errors,
        skipped=total_skipped,
        time=total_time,
        pass_pct=pass_pct,
        fail_pct=fail_pct,
        skip_pct=skip_pct,
        status_class=status_class,
        status_text=status_text,
        suite_count=len(all_suites),
        failed_section=failed_section,
        suites_html=suites_html
    )

    # Escribir archivo
    output_path = Path(output_dir) / "index.html"
    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text(html_content, encoding='utf-8')

    print(f"Dashboard HTML generado: {output_path}")
    print(f"  Suites: {len(all_suites)}")
    print(f"  Tests: {total_tests} (✅ {total_ran - total_failures - total_errors}, ❌ {total_failures}, 💥 {total_errors}, ⏭️ {total_skipped})")
    print(f"  Tiempo: {total_time:.2f}s")

    return total_failures + total_errors


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser(description="Genera dashboard HTML desde JUnit XML")
    parser.add_argument("--surefire", default="target/surefire-reports", help="Directorio Surefire")
    parser.add_argument("--failsafe", default="target/failsafe-reports", help="Directorio Failsafe")
    parser.add_argument("--output", default="reports/html", help="Directorio de salida HTML")

    args = parser.parse_args()

    error_count = generate_dashboard([args.surefire, args.failsafe], args.output)
    sys.exit(1 if error_count > 0 else 0)

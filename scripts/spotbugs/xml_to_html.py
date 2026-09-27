#!/usr/bin/env python3
"""Convierte target/spotbugsXml.xml a un HTML simple y legible, sin depender
del XSLT oficial de SpotBugs (requiere XSLT 2.0/Saxon; no compila con el
procesador 1.0 de lxml/libxslt). Salida: target/spotbugs.html
"""
import html
import os
import sys
import xml.etree.ElementTree as ET

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
XML_IN = os.path.join(ROOT, "target", "spotbugsXml.xml")
HTML_OUT = os.path.join(ROOT, "target", "spotbugs.html")

PRIORITY_NAMES = {"1": "High", "2": "Normal", "3": "Low"}


def render(bugs):
    rows = []
    for bug in bugs:
        cls = bug.find("Class")
        method = bug.find("Method")
        source_line = bug.find("SourceLine")
        location = cls.get("classname") if cls is not None else "?"
        if method is not None:
            location += f".{method.get('name')}"
        if source_line is not None and source_line.get("start"):
            location += f" (línea {source_line.get('start')})"
        message_elem = bug.find("LongMessage")
        message = message_elem.text if message_elem is not None else bug.get("type")
        rows.append({
            "type": bug.get("type"),
            "category": bug.get("category"),
            "priority": PRIORITY_NAMES.get(bug.get("priority"), bug.get("priority")),
            "location": location,
            "message": message,
        })

    rows.sort(key=lambda r: (r["priority"], r["category"]))

    body_rows = "\n".join(
        f"<tr><td>{html.escape(r['priority'])}</td>"
        f"<td>{html.escape(r['category'])}</td>"
        f"<td>{html.escape(r['type'])}</td>"
        f"<td>{html.escape(r['location'])}</td>"
        f"<td>{html.escape(r['message'] or '')}</td></tr>"
        for r in rows
    )

    return f"""<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8">
<title>Informe SpotBugs</title>
<style>
body {{ font-family: Arial, Helvetica, sans-serif; margin: 2rem; }}
table {{ border-collapse: collapse; width: 100%; }}
th, td {{ border: 1px solid #ccc; padding: 6px 10px; text-align: left; vertical-align: top; }}
th {{ background: #f0f0f0; }}
tr:nth-child(even) {{ background: #fafafa; }}
</style>
</head>
<body>
<h1>Informe SpotBugs</h1>
<p>Total de hallazgos: {len(rows)}</p>
<table>
<thead><tr><th>Prioridad</th><th>Categoría</th><th>Tipo</th><th>Ubicación</th><th>Mensaje</th></tr></thead>
<tbody>
{body_rows}
</tbody>
</table>
</body>
</html>
"""


if __name__ == "__main__":
    if not os.path.exists(XML_IN):
        print(f"No existe {XML_IN}; ejecuta antes `mvn spotbugs:spotbugs`.", file=sys.stderr)
        sys.exit(1)

    root = ET.parse(XML_IN).getroot()
    bugs = root.findall("BugInstance")
    with open(HTML_OUT, "w", encoding="utf-8") as f:
        f.write(render(bugs))
    print(f"SpotBugs HTML -> {HTML_OUT} ({len(bugs)} hallazgos)")

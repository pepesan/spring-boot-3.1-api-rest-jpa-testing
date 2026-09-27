#!/usr/bin/env bash
# Análisis estático con SpotBugs. Informes:
#   - target/spotbugsXml.xml  (XML)
#   - target/spotbugs.html    (HTML, generado localmente porque el XSLT oficial
#     de SpotBugs requiere XSLT 2.0/Saxon y no compila con lxml/libxslt)
set -euo pipefail
cd "$(dirname "$0")/.."

./mvnw -B spotbugs:spotbugs
python3 scripts/spotbugs/xml_to_html.py

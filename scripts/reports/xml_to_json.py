#!/usr/bin/env python3
"""Convierte los informes XML que ya genera Maven (JUnit de Surefire, JaCoCo)
a JSON, para tener también ese formato sin depender de herramientas externas.

Entrada:
  - target/surefire-reports/TEST-*.xml  (JUnit XML, uno por clase de test)
  - target/site/jacoco/jacoco.xml       (cobertura de código)

Salida:
  - target/reports-json/junit-summary.json
  - target/reports-json/jacoco-summary.json
"""
import glob
import json
import os
import sys
import xml.etree.ElementTree as ET

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
TARGET = os.path.join(ROOT, "target")
OUT_DIR = os.path.join(TARGET, "reports-json")


def convert_junit():
    files = sorted(glob.glob(os.path.join(TARGET, "surefire-reports", "TEST-*.xml")))
    classes = []
    totals = {"tests": 0, "failures": 0, "errors": 0, "skipped": 0, "time": 0.0}

    for path in files:
        tree = ET.parse(path)
        root = tree.getroot()
        entry = {
            "className": root.get("name"),
            "tests": int(root.get("tests", 0)),
            "failures": int(root.get("failures", 0)),
            "errors": int(root.get("errors", 0)),
            "skipped": int(root.get("skipped", 0)),
            "time": float(root.get("time", 0.0)),
            "testCases": [],
        }
        for tc in root.findall("testcase"):
            case = {
                "name": tc.get("name"),
                "time": float(tc.get("time", 0.0)),
                "status": "passed",
            }
            if tc.find("failure") is not None:
                case["status"] = "failed"
            elif tc.find("error") is not None:
                case["status"] = "error"
            elif tc.find("skipped") is not None:
                case["status"] = "skipped"
            entry["testCases"].append(case)
        classes.append(entry)

        for key in ("tests", "failures", "errors", "skipped"):
            totals[key] += entry[key]
        totals["time"] += entry["time"]

    summary = {"totals": totals, "classes": classes}
    os.makedirs(OUT_DIR, exist_ok=True)
    out_path = os.path.join(OUT_DIR, "junit-summary.json")
    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(summary, f, ensure_ascii=False, indent=2)
    print(f"JUnit -> {out_path} ({len(classes)} clases, {totals['tests']} tests)")


def _counters(elem):
    result = {}
    for counter in elem.findall("counter"):
        counter_type = counter.get("type").lower()
        covered = int(counter.get("covered", 0))
        missed = int(counter.get("missed", 0))
        total = covered + missed
        result[counter_type] = {
            "covered": covered,
            "missed": missed,
            "total": total,
            "coveragePct": round((covered / total * 100), 2) if total else None,
        }
    return result


def convert_jacoco():
    path = os.path.join(TARGET, "site", "jacoco", "jacoco.xml")
    if not os.path.exists(path):
        print(f"(sin informe JaCoCo en {path}, se omite)")
        return

    # jacoco.xml declara un DOCTYPE con DTD externa; no hace falta resolverla
    # para leer los datos, así que se parsea ignorando entidades externas.
    parser = ET.XMLParser()
    tree = ET.parse(path, parser=parser)
    root = tree.getroot()

    packages = []
    for package in root.findall("package"):
        packages.append({
            "name": package.get("name").replace("/", "."),
            "counters": _counters(package),
        })

    summary = {
        "report": root.get("name"),
        "overall": _counters(root),
        "packages": packages,
    }
    os.makedirs(OUT_DIR, exist_ok=True)
    out_path = os.path.join(OUT_DIR, "jacoco-summary.json")
    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(summary, f, ensure_ascii=False, indent=2)
    line = summary["overall"].get("line", {})
    print(f"JaCoCo -> {out_path} (líneas cubiertas: {line.get('coveragePct')}%)")


if __name__ == "__main__":
    try:
        convert_junit()
        convert_jacoco()
    except Exception as exc:  # noqa: BLE001 - script de utilidad, se quiere el error visible
        print(f"Error generando JSON de informes: {exc}", file=sys.stderr)
        sys.exit(1)

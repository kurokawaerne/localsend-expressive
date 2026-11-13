#!/usr/bin/env python3
"""Prune unused Material Symbols icon classes from the two Composables
icon jars in a built desktop distribution.

Each icon is one top-level `<Name>Kt.class` (plus possible inner
classes). Icons are referenced from Kotlin source either qualified
(MaterialSymbols.Outlined.<Name>) or through wildcard imports by bare
name. The keep-set is therefore: every icon name that appears as a
whole word in ANY .kt source of the repo — a superset of real usage,
so pruning can only remove classes nothing references. All other jar
entries (registry objects, META-INF) are kept untouched.
"""
import re
import sys
import zipfile
from pathlib import Path

def icon_names(jar_path: Path, prefix: str) -> set[str]:
    names = set()
    with zipfile.ZipFile(jar_path) as z:
        for n in z.namelist():
            if n.startswith(prefix) and n.endswith(".class"):
                base = n[len(prefix):-len(".class")].split("$")[0]
                if base.endswith("Kt"):
                    names.add(base[:-2])
    return names

def main() -> int:
    repo = Path(sys.argv[1])          # repo root (sources)
    jar = Path(sys.argv[2])           # icon jar to prune in place
    prefix = sys.argv[3]              # icon package prefix inside the jar

    words: set[str] = set()
    for kt in repo.rglob("*.kt"):
        if "/build/" in str(kt):
            continue
        words.update(
            re.findall(
                r"[A-Za-z_][A-Za-z_0-9]*",
                kt.read_text(encoding="utf-8", errors="ignore"),
            )
        )

    all_icons = icon_names(jar, prefix)
    keep = all_icons & words
    drop = all_icons - keep

    before = jar.stat().st_size
    tmp = jar.with_suffix(".pruned")
    dropped_files = 0
    with zipfile.ZipFile(jar) as zin, zipfile.ZipFile(
        tmp, "w", zipfile.ZIP_DEFLATED
    ) as zout:
        for item in zin.infolist():
            n = item.filename
            if n.startswith(prefix) and n.endswith(".class"):
                base = n[len(prefix):-len(".class")].split("$")[0]
                if base.endswith("Kt") and base[:-2] in drop:
                    dropped_files += 1
                    continue
            zout.writestr(item, zin.read(n))
    tmp.replace(jar)
    after = jar.stat().st_size
    print(
        f"{jar.name}: icons kept={len(keep)} dropped={len(drop)} "
        f"files dropped={dropped_files} size {before/1e6:.2f}MB -> {after/1e6:.2f}MB"
    )
    # hard verification: every kept icon's class must still be present
    with zipfile.ZipFile(jar) as z:
        names = set(z.namelist())
    missing = [n for n in keep if f"{prefix}{n}Kt.class" not in names]
    if missing:
        print("ERROR: kept icons missing after prune:", missing[:10])
        return 1
    return 0

if __name__ == "__main__":
    raise SystemExit(main())

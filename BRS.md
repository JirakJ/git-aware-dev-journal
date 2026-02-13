# Git-Aware Dev Journal — Business Requirements Specification (BRS)

**Verze:** 2026.2.1
**Autor:** Jakub Jirák
**Datum:** 12. února 2026
**Model:** Freemium JetBrains Plugin

---

## 1. Executive Summary

Git-Aware Dev Journal je IDE-nativní vývojářský deník s plnou Git integrací. Řeší problém ztracené kontextové znalosti — proč byl kód napsán, jaká rozhodnutí vedla k implementaci. Každá poznámka je propojena s commit hashem, větví a kódem. Žádný přímý konkurent na JetBrains Marketplace.

---

## 2. Obchodní cíle

### 2.1 Primární role
**Standalone monetizovaný produkt** — freemium model s jasnou hodnotovou propozicí pro individuální vývojáře i týmy. Deep Git integrace jako hlavní konkurenční výhoda, zero context switching.

### 2.2 KPI
| Metrika | Cíl (6 měsíců) | Cíl (12 měsíců) |
|---------|:---:|:---:|
| Celkové instalace | 8 000 | 30 000 |
| MAU | 4 000 | 15 000 |
| DAU | 1 500 | 6 000 |
| Poznámky/uživatel/den | ≥ 2 | ≥ 3 |
| Pro konverze | 5 % | 8 % |
| Retention (30-day) | 40 % | 55 % |
| Marketplace rating | ≥ 4.3 | ≥ 4.5 |

---

## 3. Cílový trh

- **Individuální vývojáři** — dokumentace rozhodnutí, onboarding poznámky, osobní knowledge base
- **Malé týmy (2–10 lidí)** — sdílené poznámky, standup generátor, PR summaries
- **Tech leads & senior vývojáři** — přehled o práci, code review context, branch narrativy
- **Cílová velikost trhu:** ~2M JetBrains uživatelů s aktivním Git workflow

---

## 4. Konkurenční prostředí

Na JetBrains Marketplace **neexistuje přímý konkurent** pro commit-linked development journals. Existující nástroje:

| Nástroj | Typ | Nevýhoda vs. Dev Journal |
|---------|-----|--------------------------|
| Notion / Obsidian | Externí | Context switching, žádná Git integrace |
| Git commit messages | Nativní | Neumožňuje retrospektivní poznámky |
| Code comments | Nativní | Znečišťuje kód, chybí timeline |
| Jira / Linear | Projektové | Příliš high-level, žádná code-level vazba |

### Konkurenční výhoda
- **Deep Git integrace** — commit linking, blame, branch narrativy
- **Zero context switching** — vše v IDE
- **Automatický capture** — conventional commits parsing
- **Team value** — sdílené poznámky, standup generátor

---

## 5. Revenue model

| Tier | Cena | Obsah |
|------|------|-------|
| **Free** | $0 | Lokální poznámky, 50/projekt, commit linking, blame view |
| **Pro** | $5/měsíc nebo $49/rok | Sync, unlimited, team sharing, standup generator, PR summary |
| **Enterprise** | $12/dev/měsíc | Audit trail, compliance, admin dashboard, SSO |

### Projekce příjmů (12 měsíců)
| Metrika | Hodnota |
|---------|---------|
| Celkové instalace | 30 000 |
| Pro konverze (8 %) | 2 400 |
| MRR (Pro) | $12 000 |
| Enterprise zákazníci (odhad) | 10 týmů × 8 dev |
| MRR (Enterprise) | $960 |
| **Celkové MRR** | **~$13 000** |

---

## 6. Go-to-Market

1. **Developer-first launch** — Product Hunt, Reddit r/programming, r/IntelliJIDEA, Hacker News
2. **Content marketing** — blog post: "Stop losing code context: Git-aware journaling"
3. **Social proof** — open source `.dev-journal/` formát → komunita
4. **Partnerships** — integrace s populárními Git workflow nástroji
5. **Virální loop** — sdílené `.dev-journal/` v repo → kolegové vidí hodnotu → instalace

---

## 7. Milníky

| Milestone | Popis |
|-----------|-------|
| **M1: MVP** | Commit-linked notes, tool window, manual entry, JSON storage |
| **M2: Auto-Capture** | Conventional commit parser, auto-categorization |
| **M3: Branch Story** | Branch narrative generation, timeline view |
| **M4: Code Hover** | "Why did I write this?" hover, blame integration |
| **M5: Standup** | Standup generator, PR/MR summary generator |
| **M6: Team** | Team sharing, sync, Pro licensing |
| **M7: Enterprise** | Audit trail, compliance, admin dashboard |

---

## 8. Success Criteria

1. 30 000+ instalací do 12 měsíců
2. Pro konverze ≥ 8 % (MRR $12 000+)
3. DAU ≥ 6 000 (denní aktivní uživatelé)
4. ≥ 3 poznámky/uživatel/den průměrně
5. Marketplace rating ≥ 4.5
6. 30-day retention ≥ 55 %

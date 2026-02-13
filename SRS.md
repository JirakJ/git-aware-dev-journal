# Git-Aware Dev Journal — Software Requirements Specification (SRS)

**Verze:** 2026.2.1
**Autor:** Jakub Jirák
**Datum:** 12. února 2026
**Model:** Freemium JetBrains Plugin

---

## 1. Úvod

### 1.1 Účel
Git-Aware Dev Journal je IDE-nativní vývojářský deník s plnou Git integrací. Každá poznámka je propojena s commit hashem, větví a kontextem kódu. Plugin automaticky zachycuje vývojový příběh z historie commitů a umožňuje vývojářům dokumentovat rozhodnutí přímo v IDE.

### 1.2 Rozsah
Plugin sleduje Git historii, parsuje conventional commits, generuje branch narrativy a umožňuje manuální poznámky propojené s konkrétními commity. Data se ukládají do `.dev-journal/` adresáře v project root jako Git-tracked JSON soubory.

### 1.3 Reference
- Conventional Commits specifikace: https://www.conventionalcommits.org/
- Git4Idea API (JetBrains bundled Git plugin)

---

## 2. Celkový popis

### 2.1 Perspektiva produktu
Standalone freemium plugin pro JetBrains IDE. Řeší problém ztracené kontextové znalosti — proč byl kód napsán, jaká rozhodnutí vedla k implementaci, co se dělo na branch. Žádný přímý konkurent na JetBrains Marketplace pro commit-linked journals.

### 2.2 Uživatelské skupiny
- **Individuální vývojáři** — dokumentace rozhodnutí, onboarding notes, code context
- **Malé týmy** — sdílené poznámky, standup generátor, PR summaries
- **Tech leads** — přehled o práci týmu, branch narrativy, code review context

---

## 3. Funkční požadavky

### FR-1: Commit-Linked Notes
- **FR-1.1:** Každá poznámka je propojena se specifickým commit hashem
- **FR-1.2:** Poznámky jsou viditelné při hoveru nad kódem (annotace)
- **FR-1.3:** Poznámky jsou viditelné v blame view (Git blame integrace)
- **FR-1.4:** Manuální přidání poznámky ke commitu přes dialog nebo tool window
- **FR-1.5:** Automatické propojení na aktuální HEAD commit při vytvoření poznámky

### FR-2: Auto-Capture z Conventional Commits
- **FR-2.1:** Parsování commit messages: `feat:`, `fix:`, `chore:`, `refactor:`, `docs:`, `test:`, `perf:`
- **FR-2.2:** Extrakce kontextu — scope, breaking changes, body, footer
- **FR-2.3:** Automatická kategorizace: FEATURE, BUGFIX, MAINTENANCE, REFACTOR, DOCS, TEST, PERFORMANCE
- **FR-2.4:** Konfigurovatelné custom prefixy
- **FR-2.5:** Auto-capture probíhá na pozadí při nových commitech (post-commit hook nebo polling)

### FR-3: Branch Narrative
- **FR-3.1:** Automatický příběh feature branch — od vytvoření po merge
- **FR-3.2:** Timeline commitů s extrahovaným kontextem
- **FR-3.3:** Statistiky branch: počet commitů, doba trvání, soubory změněné
- **FR-3.4:** Markdown export branch narrative

### FR-4: PR/MR Summary Generator
- **FR-4.1:** Generuje release notes z commit history + poznámek
- **FR-4.2:** Seskupení podle typu (features, fixes, chores)
- **FR-4.3:** Markdown formát kompatibilní s GitHub/GitLab PR descriptions
- **FR-4.4:** Kopírování do schránky jedním klikem

### FR-5: "Why Did I Write This?" — Code Hover
- **FR-5.1:** Hover nad kódem → zobrazí poznámky z doby, kdy byl kód modifikován
- **FR-5.2:** Integrace s `EditorHoverProvider` nebo `AnnotationGutterProvider`
- **FR-5.3:** Zobrazení: commit hash, autor, datum, poznámka, typ commitu
- **FR-5.4:** Klik na poznámku → navigace na commit detail

### FR-6: Git Blame Integration
- **FR-6.1:** Poznámky viditelné v blame view jako extra sloupec
- **FR-6.2:** Ikona indikující existenci poznámky u řádku
- **FR-6.3:** Klik na ikonu → popup s poznámkami pro daný commit

### FR-7: Standup Generator (Pro)
- **FR-7.1:** Generuje "What I did yesterday" z commitů + poznámek
- **FR-7.2:** Konfigurovatelný časový rozsah (1 den, 2 dny, custom)
- **FR-7.3:** Formát: Markdown, plain text, nebo Slack-compatible
- **FR-7.4:** Filtr podle branch a autora

### FR-8: Tool Window
- **FR-8.1:** Hlavní tool window `Dev Journal` v pravém panelu
- **FR-8.2:** Timeline view — chronologický přehled poznámek a commitů
- **FR-8.3:** Branch filtr — zobrazení per branch
- **FR-8.4:** Author filtr — zobrazení per autor
- **FR-8.5:** Full-text search přes poznámky
- **FR-8.6:** Add Entry dialog — manuální přidání poznámky
- **FR-8.7:** Entry detail — zobrazení a editace poznámky
- **FR-8.8:** Export — Markdown, JSON, CSV

### FR-9: Storage
- **FR-9.1:** Úložiště: `.dev-journal/` adresář v project root
- **FR-9.2:** Git-tracked JSON soubory (per branch)
- **FR-9.3:** Formát: `<branch-name>.json` — pole `JournalEntry` objektů
- **FR-9.4:** Automatický `.gitignore` entry pro `.dev-journal/*.lock`
- **FR-9.5:** Merge-friendly formát (one entry per line, sorted by timestamp)

---

## 4. Nefunkční požadavky

### NFR-1: Výkon
- Git operace na background threadech — nesmí blokovat EDT
- Indexování poznámek: < 500ms pro 1000 poznámek
- Hover response: < 100ms
- Standup generace: < 2 sekundy

### NFR-2: Kompatibilita
- IntelliJ IDEA 2024.3+ (sinceBuild=243, untilBuild=252.*)
- Všechny JetBrains IDE s Git podporou
- macOS, Linux, Windows
- JVM 17, Kotlin 2.1.0

### NFR-3: Závislosti
- `com.intellij.modules.platform` — povinná
- `Git4Idea` — optional (degradace bez Git)

---

## 5. Datový model

### 5.1 JournalEntry
```
JournalEntry {
  id: UUID
  commitHash: String?
  branchName: String
  authorName: String
  authorEmail: String
  timestamp: Instant
  type: EntryType
  title: String
  content: String
  tags: List<String>
  filePaths: List<String>
  lineRange: IntRange?
  parentEntryId: UUID?
}
```

### 5.2 EntryType
```
EntryType {
  MANUAL          — manuálně přidaná poznámka
  AUTO_COMMIT     — automaticky extrahovaná z commit message
  AUTO_BRANCH     — automaticky generovaný branch narrative
  STANDUP         — generovaný standup report
  PR_SUMMARY      — generovaný PR/MR summary
}
```

### 5.3 BranchNarrative
```
BranchNarrative {
  branchName: String
  baseBranch: String
  createdAt: Instant
  mergedAt: Instant?
  commits: List<CommitInfo>
  entries: List<JournalEntry>
  summary: String
  stats: BranchStats
}

BranchStats {
  totalCommits: Int
  filesChanged: Int
  insertions: Int
  deletions: Int
  duration: Duration
  authors: List<String>
}
```

---

## 6. Technická specifikace

### 6.1 Plugin identifikace
- **Package:** `com.jakubjirak.devjournal`
- **Plugin ID:** `com.jakubjirak.devjournal`
- **Version:** `2026.2.1`

### 6.2 Balíčková struktura
```
com.jakubjirak.devjournal
  ├── .model        — JournalEntry, EntryType, BranchNarrative, BranchStats
  ├── .storage       — JournalStore, JSON persistence
  ├── .git           — GitIntegrationService, ConventionalCommitParser
  ├── .narrative     — BranchNarrativeService, PrSummaryGenerator
  ├── .standup       — StandupGenerator
  ├── .ui            — JournalToolWindow, AddEntryDialog, TimelinePanel
  ├── .hover         — JournalAnnotationProvider, BlameIntegration
  ├── .settings      — DevJournalSettings, DevJournalConfigurable
  └── .licensing     — FreemiumGate, LicenseChecker
```

---

## 7. Freemium model

### Free tier
- Lokální poznámky (manuální i auto-capture)
- Max 50 poznámek na projekt
- Commit linking a blame view
- Branch narrative (read-only)
- Tool window s timeline view
- Basic search

### Pro tier ($5/měsíc)
- Sync poznámek mezi zařízeními
- Unlimited poznámky
- Team sharing (sdílené `.dev-journal/`)
- Standup generator
- PR/MR summary generator
- Pokročilý search a filtry
- Export (Markdown, JSON, CSV)

### Enterprise tier ($12/dev/měsíc)
- Audit trail — kdo co kdy přidal/změnil
- Compliance export
- Admin dashboard
- Custom retention policies
- SSO integrace

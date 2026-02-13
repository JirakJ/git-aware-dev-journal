# Git-Aware Dev Journal — Implementační plán

**Verze:** 2026.2.1
**Status:** Připraveno k implementaci

---

## Fáze 0: Plugin Ikona & Branding

- [ ] Navrhnout plugin ikonu (SVG + PNG)
  - [ ] Koncept: 📓 deník s Git branch symbolem
  - [ ] Hlavní ikona: 40×40 SVG pro Marketplace listing
  - [ ] Toolbar ikona: 16×16 + 13×13 SVG (light + dark varianta)
  - [ ] Tool window ikona: 13×13 SVG
  - [ ] `src/main/resources/META-INF/pluginIcon.svg` (40×40, light theme)
  - [ ] `src/main/resources/META-INF/pluginIcon_dark.svg` (40×40, dark theme)
  - [ ] Action ikony pro toolbar/menu (16×16 SVG, light + dark)
- [ ] Barevná paleta (zelená/modrá — Git + Journal)
- [ ] Marketplace banner obrázek (1280×800)
- [ ] Screenshoty pro Marketplace listing (min 3, 1280×800 PNG)

## Fáze 1: Projekt scaffolding

- [ ] Inicializovat IntelliJ Platform Plugin projekt (Gradle Kotlin DSL)
  - [ ] `build.gradle.kts` — IntelliJ Platform 2024.3+, JVM 17, Kotlin 2.1.0
  - [ ] `settings.gradle.kts`, `gradle.properties` (verze `2026.2.1`)
  - [ ] Gradle wrapper (8.x)
  - [ ] sinceBuild=243, untilBuild=252.*
- [ ] `src/main/resources/META-INF/plugin.xml`
  - [ ] Plugin ID: `com.jakubjirak.devjournal`
  - [ ] Závislosti: `com.intellij.modules.platform`, optional `Git4Idea`
- [ ] Balíčková struktura: `com.jakubjirak.devjournal.*`
  - [ ] `.model` — datové třídy (JournalEntry, EntryType, BranchNarrative)
  - [ ] `.storage` — JournalStore, JSON persistence
  - [ ] `.git` — Git integrace, ConventionalCommitParser
  - [ ] `.narrative` — BranchNarrativeService, PrSummaryGenerator
  - [ ] `.standup` — StandupGenerator
  - [ ] `.ui` — Tool window, timeline, dialogy
  - [ ] `.hover` — Code hover, blame integrace
  - [ ] `.settings` — konfigurace
  - [ ] `.licensing` — FreemiumGate, LicenseChecker
- [ ] `src/main/resources/messages/DevJournalBundle.properties`
- [ ] `.gitignore` (build/, .idea/, .gradle/, .kotlin/, out/)
- [ ] `gradle/libs.versions.toml` — version catalog
- [ ] `LICENSE` (Apache-2.0)
- [ ] `.editorconfig` (Kotlin style conventions)
- [ ] Detekt konfigurace (`detekt.yml`)
- [ ] GitHub Actions CI workflow (`.github/workflows/build.yml`)
- [ ] Ověřit `./gradlew build`

## Fáze 2: Datový model

- [ ] `JournalEntry.kt`
  - [ ] `id: UUID`
  - [ ] `commitHash: String?` — propojení na konkrétní commit
  - [ ] `branchName: String`
  - [ ] `authorName: String`, `authorEmail: String`
  - [ ] `timestamp: Instant`
  - [ ] `type: EntryType`
  - [ ] `title: String`, `content: String`
  - [ ] `tags: List<String>`
  - [ ] `filePaths: List<String>`, `lineRange: IntRange?`
  - [ ] `parentEntryId: UUID?`
- [ ] `EntryType.kt` — enum
  - [ ] MANUAL, AUTO_COMMIT, AUTO_BRANCH, STANDUP, PR_SUMMARY
- [ ] `BranchNarrative.kt`
  - [ ] `branchName: String`, `baseBranch: String`
  - [ ] `createdAt: Instant`, `mergedAt: Instant?`
  - [ ] `commits: List<CommitInfo>`, `entries: List<JournalEntry>`
  - [ ] `summary: String`, `stats: BranchStats`
- [ ] `BranchStats.kt`
  - [ ] `totalCommits: Int`, `filesChanged: Int`
  - [ ] `insertions: Int`, `deletions: Int`
  - [ ] `duration: Duration`, `authors: List<String>`
- [ ] `CommitInfo.kt`
  - [ ] `hash: String`, `shortHash: String`
  - [ ] `message: String`, `author: String`, `timestamp: Instant`
  - [ ] `conventionalType: String?`, `scope: String?`
- [ ] Unit testy

## Fáze 3: Storage (JournalStore, JSON persistence)

- [ ] `JournalStore.kt` — project-level service
  - [ ] `fun addEntry(entry: JournalEntry)`
  - [ ] `fun getEntries(branch: String?): List<JournalEntry>`
  - [ ] `fun getEntriesByCommit(commitHash: String): List<JournalEntry>`
  - [ ] `fun getEntriesByFile(filePath: String): List<JournalEntry>`
  - [ ] `fun updateEntry(entry: JournalEntry)`
  - [ ] `fun deleteEntry(id: UUID)`
  - [ ] `fun search(query: String): List<JournalEntry>`
- [ ] JSON persistence engine
  - [ ] Úložiště: `.dev-journal/<branch-name>.json`
  - [ ] Merge-friendly formát (one entry per line, sorted by timestamp)
  - [ ] Kotlinx Serialization pro JSON serde
  - [ ] File locking (`.dev-journal/*.lock`)
  - [ ] Auto-create `.dev-journal/` adresář
- [ ] `JournalIndex.kt` — in-memory index pro rychlý lookup
  - [ ] Index by commit hash
  - [ ] Index by file path
  - [ ] Index by timestamp
  - [ ] Lazy loading při prvním přístupu
- [ ] Registrace jako `ProjectService` v `plugin.xml`
- [ ] Unit testy (CRUD operace, persistence, index, search)

## Fáze 4: Git Integration (commit linking, blame, branch detekce)

- [ ] `GitIntegrationService.kt` — project-level service
  - [ ] `fun getCurrentBranch(): String?`
  - [ ] `fun getHeadCommit(): CommitInfo?`
  - [ ] `fun getCommitHistory(branch: String, limit: Int): List<CommitInfo>`
  - [ ] `fun getBlameInfo(file: VirtualFile, line: Int): CommitInfo?`
  - [ ] `fun getBranchCommits(branch: String, baseBranch: String): List<CommitInfo>`
  - [ ] `fun getFileHistory(file: VirtualFile): List<CommitInfo>`
  - [ ] Integrace s `Git4Idea` API (optional dependency)
  - [ ] Graceful degradation bez Git4Idea
- [ ] `BranchDetector.kt`
  - [ ] Detekce aktuální větve (`.git/HEAD` monitoring)
  - [ ] Event: `BranchChangedEvent`
  - [ ] File watcher na `.git/HEAD`
- [ ] `CommitListener.kt`
  - [ ] Listener na nové commity (VCS commit events)
  - [ ] Trigger auto-capture při novém commitu
- [ ] Unit testy (mock Git4Idea)

## Fáze 5: Auto-Capture (conventional commit parser)

- [ ] `ConventionalCommitParser.kt`
  - [ ] `fun parse(message: String): ConventionalCommit?`
  - [ ] Parsování: `type(scope): description`
  - [ ] Podporované typy: `feat`, `fix`, `chore`, `refactor`, `docs`, `test`, `perf`
  - [ ] Extrakce: type, scope, description, body, footer
  - [ ] Breaking changes: `!` suffix nebo `BREAKING CHANGE:` footer
- [ ] `ConventionalCommit.kt`
  - [ ] `type: String`, `scope: String?`
  - [ ] `description: String`, `body: String?`
  - [ ] `isBreaking: Boolean`, `footers: Map<String, String>`
- [ ] `AutoCaptureService.kt`
  - [ ] Automatická tvorba `JournalEntry` z parsed commitů
  - [ ] Mapování type → EntryType kategorie
  - [ ] Deduplikace (nepřidávat entry pro již zpracované commity)
  - [ ] Konfigurovatelné zapnutí/vypnutí
- [ ] Unit testy (parsing edge cases, auto-capture flow)

## Fáze 6: UI (Tool window, timeline, add entry dialog)

- [ ] `JournalToolWindowFactory.kt` implements `ToolWindowFactory`
  - [ ] Registrace v `plugin.xml`: `<toolWindow id="Dev Journal" ...>`
  - [ ] Pozice: RIGHT
  - [ ] Ikona: 13×13 SVG
- [ ] `TimelinePanel.kt` — hlavní obsah tool window
  - [ ] Chronologický seznam poznámek a commitů
  - [ ] Entry rendering: ikona typu, title, timestamp, commit hash
  - [ ] Klik na entry → detail panel
  - [ ] Kontextové menu: edit, delete, copy, export
- [ ] `BranchFilterPanel.kt`
  - [ ] Dropdown pro výběr branch
  - [ ] "All branches" option
  - [ ] Aktuální branch preselected
- [ ] `AuthorFilterPanel.kt`
  - [ ] Dropdown pro výběr autora
  - [ ] "All authors" option
- [ ] `SearchPanel.kt`
  - [ ] Full-text search přes poznámky
  - [ ] Real-time filtrování
- [ ] `AddEntryDialog.kt` extends `DialogWrapper`
  - [ ] Title input
  - [ ] Content textarea (Markdown support)
  - [ ] Tags input (chip style)
  - [ ] Commit hash (auto-filled z HEAD, editable)
  - [ ] File path selector (optional)
  - [ ] Entry type selector (default: MANUAL)
- [ ] `EntryDetailPanel.kt`
  - [ ] Zobrazení a editace poznámky
  - [ ] Commit info (hash, autor, datum, message)
  - [ ] Navigace na soubor/řádek
- [ ] Action: `AddJournalEntryAction` — toolbar + klávesová zkratka
- [ ] Unit testy (dialog validation)

## Fáze 7: Code Hover Integration ("Why did I write this?")

- [ ] `JournalAnnotationProvider.kt`
  - [ ] Implementace `LineMarkerProvider` nebo `EditorHoverProvider`
  - [ ] Lookup: aktuální řádek → blame → commit hash → journal entries
  - [ ] Zobrazení: commit hash, autor, datum, poznámka, typ commitu
  - [ ] Klik → navigace na entry v tool window
- [ ] `JournalGutterIconRenderer.kt`
  - [ ] Ikona v gutter pro řádky s poznámkami
  - [ ] Tooltip s preview poznámky
  - [ ] Klik → popup s detailem
- [ ] `BlameEnrichmentProvider.kt`
  - [ ] Rozšíření blame view o journal entries
  - [ ] Extra sloupec s ikonou poznámky
- [ ] Performance: lazy loading, caching blame info
- [ ] Unit testy

## Fáze 8: Standup Generator

- [ ] `StandupGenerator.kt`
  - [ ] `fun generate(since: Instant, until: Instant, author: String?, branch: String?): StandupReport`
  - [ ] Agregace commitů + poznámek za časové období
  - [ ] Seskupení podle projektu/branch
  - [ ] Formátování: Markdown, plain text, Slack-compatible
- [ ] `StandupReport.kt`
  - [ ] `entries: List<StandupItem>`, `period: DateRange`
  - [ ] `fun toMarkdown(): String`
  - [ ] `fun toPlainText(): String`
  - [ ] `fun toSlack(): String`
- [ ] `PrSummaryGenerator.kt`
  - [ ] `fun generate(branch: String, baseBranch: String): PrSummary`
  - [ ] Seskupení commitů podle typu (Features, Fixes, Other)
  - [ ] Markdown formát pro GitHub/GitLab PR description
  - [ ] Kopírování do schránky
- [ ] `StandupGeneratorPanel.kt` — UI panel v tool window
  - [ ] Date range selector
  - [ ] Author filtr
  - [ ] Format selector (Markdown / Plain text / Slack)
  - [ ] Generate + Copy tlačítka
- [ ] Unit testy (generace, formátování)

## Fáze 9: Konfigurace

- [ ] `DevJournalSettings.kt` implements `PersistentStateComponent`
  - [ ] Auto-capture enable/disable (default: true)
  - [ ] Conventional commit types (konfigurovatelné)
  - [ ] Max entries per project (free tier: 50)
  - [ ] Storage path override (default: `.dev-journal/`)
  - [ ] Standup default time range
  - [ ] Gutter icons enable/disable
  - [ ] Hover annotations enable/disable
- [ ] `DevJournalConfigurable.kt` — Settings → Tools → Dev Journal
  - [ ] General tab: auto-capture, storage path
  - [ ] Conventional Commits tab: type mapping, custom types
  - [ ] Display tab: gutter icons, hover, tool window position
  - [ ] Standup tab: default time range, format, author
- [ ] Unit testy

## Fáze 10: Freemium & Licensing Infrastructure

- [ ] Integrace JetBrains Marketplace Licensing API
  - [ ] `LicenseChecker.kt` — ověření licence přes Marketplace API
  - [ ] `LicenseStatus.kt` — enum: FREE, TRIAL, PRO, ENTERPRISE, EXPIRED
  - [ ] `FreemiumGate.kt` — centrální guard pro paid features
    - [ ] `fun isPaidFeatureAvailable(featureId: String): Boolean`
    - [ ] Graceful degradation — paid UI elementy skryté/disabled ve free tieru
  - [ ] Trial period: 30 dní plný přístup
- [ ] `plugin.xml` — `<product-descriptor>` element pro Marketplace licensing
  - [ ] `code` atribut (unique product code)
  - [ ] `release-date`, `release-version`
- [ ] Nag screen / upgrade prompt
  - [ ] Non-intrusive balloon notification při pokusu o paid feature
  - [ ] „Upgrade to Pro" action → Marketplace stránka
  - [ ] Max 1× denně (ne spam)
- [ ] Feature flags
  - [ ] `FeatureRegistry.kt` — definice free vs. paid features
  - [ ] Free: lokální poznámky (50/projekt), commit linking, blame view
  - [ ] Pro: sync, unlimited, team sharing, standup generator, PR summary
  - [ ] Enterprise: audit trail, compliance, admin dashboard
- [ ] Entry count limiter pro free tier
  - [ ] Warning při 40/50 poznámkách
  - [ ] Block při 50 s upgrade prompt
- [ ] Unit testy pro licensing logic (mock Marketplace API)

## Fáze 11: Production-Grade Quality

- [ ] Error Handling & Resilience
  - [ ] Globální exception handler (`ErrorReportSubmitter` implementace)
  - [ ] Graceful degradation — plugin nesmí crashnout IDE
  - [ ] Try-catch na všech EP entry pointech
  - [ ] Timeout na všech I/O operacích (network, git, file system)
  - [ ] Rate limiting na Git operace
- [ ] Logging
  - [ ] `Logger` instance v každé třídě (IntelliJ `Logger.getInstance()`)
  - [ ] Structured logging: DEBUG pro vývoj, INFO pro operace, WARN/ERROR pro problémy
  - [ ] Žádné citlivé údaje v logech
- [ ] Telemetrie (opt-in, anonymní)
  - [ ] `PluginUsageTracker.kt` — FUS (Feature Usage Statistics) integrace
  - [ ] Metriky: poznámky/den, auto-capture rate, standup usage, error rate
  - [ ] Registrace FUS group v `plugin.xml`
- [ ] Performance
  - [ ] Profiling na velkých projektech (10k+ souborů, 1000+ poznámek)
  - [ ] Background thread pro všechny I/O operace (nesmí blokovat EDT)
  - [ ] Read/write action správně (ReadAction, WriteAction, smart mode check)
  - [ ] Disposal pattern: `Disposable` pro všechny listeners a services
  - [ ] Memory leak check (WeakReference kde potřeba)
  - [ ] Lazy loading journal entries (ne celý JSON do paměti)
- [ ] Kódová kvalita
  - [ ] Kotlin coding conventions (official JetBrains style)
  - [ ] Detekt nebo ktlint pro statickou analýzu
  - [ ] Žádné deprecated API (IntelliJ Platform compatibility)
  - [ ] `@ApiStatus.Experimental` / `@ApiStatus.Internal` anotace
- [ ] Dokumentace
  - [ ] KDoc pro všechny public API
  - [ ] README.md: instalace, konfigurace, screenshoty, FAQ
  - [ ] CHANGELOG.md (Keep a Changelog format)
  - [ ] CONTRIBUTING.md (pokud open source)

## Fáze 12: Testy

- [ ] Unit testy: ≥80 % coverage core logiky
  - [ ] `JournalEntryTest.kt` — model validace
  - [ ] `JournalStoreTest.kt` — CRUD, persistence, index, search
  - [ ] `ConventionalCommitParserTest.kt` — parsing edge cases
  - [ ] `AutoCaptureServiceTest.kt` — auto-capture flow
  - [ ] `StandupGeneratorTest.kt` — generace, formátování
  - [ ] `PrSummaryGeneratorTest.kt` — PR summary generace
  - [ ] `BranchNarrativeServiceTest.kt` — branch narrative
  - [ ] `FreemiumGateTest.kt` — licensing logic
- [ ] Integrační testy: `BasePlatformTestCase`
  - [ ] Git integrace (mock Git4Idea)
  - [ ] Storage persistence (temp directory)
  - [ ] Tool window rendering
- [ ] UI testy: `HeavyPlatformTestCase`
  - [ ] AddEntryDialog validace
  - [ ] Timeline panel rendering
  - [ ] Gutter icon click handling
- [ ] Performance testy
  - [ ] 1000+ entries loading < 500ms
  - [ ] Hover response < 100ms
  - [ ] Search response < 200ms

## Fáze 13: Release

- [ ] CI/CD Pipeline
  - [ ] GitHub Actions workflow:
    - [ ] `./gradlew check` — kompilace + testy
    - [ ] `./gradlew verifyPlugin` — plugin strukturální validace
    - [ ] `./gradlew verifyPluginProjectConfiguration` — metadata validace
    - [ ] `./gradlew runPluginVerifier` — binární kompatibilita s target IDE verzemi
    - [ ] Qodana scan (JetBrains code quality)
  - [ ] Release workflow:
    - [ ] Tag-based release (push tag → build → publish)
    - [ ] `./gradlew publishPlugin` — automatický upload na Marketplace
    - [ ] Changelog generace z git commitů
  - [ ] Dependabot / Renovate pro dependency updates
- [ ] Ukázkový `.dev-journal/` s demo entries
- [ ] README.md s screenshoty a GIFy
- [ ] `./gradlew verifyPlugin`, `./gradlew buildPlugin`
- [ ] Manuální testování s reálným Git repo
- [ ] Cross-IDE test (IntelliJ IDEA, WebStorm, PyCharm, GoLand)
- [ ] Cross-OS test (macOS, Linux, Windows)
- [ ] Marketplace listing (popis, screenshoty, tags)
- [ ] Git tag `v2026.2.1`

---

## Budoucí fáze

- [ ] AI-powered summaries (LLM integrace pro automatické shrnutí)
- [ ] Voice notes (audio poznámky konvertované na text)
- [ ] Cross-repo journal (poznámky sdílené mezi projekty)
- [ ] Slack/Teams integrace (standup report přímo do kanálu)
- [ ] VS Code extension (multi-IDE podpora)
- [ ] Web dashboard pro team overview
- [ ] Git hooks integrace (pre-commit, post-commit)

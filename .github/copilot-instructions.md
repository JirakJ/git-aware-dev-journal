# Copilot Instructions — Git-Aware Dev Journal

## Projekt
JetBrains IDE plugin — IDE-nativní vývojářský deník s plnou Git integrací. Každá poznámka je propojena s commit hashem, větví a kontextem kódu. Automaticky zachycuje vývojový příběh z historie commitů.

## Technický stack
- **Jazyk:** Kotlin (JVM 17+)
- **Build:** Gradle (Kotlin DSL) s `org.jetbrains.intellij.platform` plugin 2.x
- **Platform:** IntelliJ Platform SDK 2024.3+
- **Testování:** JUnit 5 + `BasePlatformTestCase`
- **Kotlin:** 2.1.0

## Build a testy
```bash
./gradlew build
./gradlew test
./gradlew test --tests "*.JournalStoreTest.testSaveAndLoadEntry"
./gradlew runIde
./gradlew verifyPlugin
```

## Architektura
- **Model:** `JournalEntry`, `EntryType`, `BranchNarrative` — datové třídy pro záznamy
- **Storage:** `JournalStore` — JSON persistence v `.dev-journal/` adresáři (per branch)
- **Git:** `GitIntegrationService` — commit linking, blame integrace, branch detekce
- **Parser:** `ConventionalCommitParser` — parsuje `feat:`, `fix:`, `chore:` z commit messages
- **UI:** `JournalToolWindow` (timeline view), `AddEntryDialog`, `StandupGeneratorPanel`
- **Hover:** `JournalAnnotationProvider` — "Why did I write this?" hover nad kódem
- **Generator:** `StandupGenerator` — generuje "What I did yesterday" z commitů + poznámek
- **Generator:** `PrSummaryGenerator` — generuje PR/MR summary z commit history + poznámek

## Konvence
- Balíčky: `com.jakubjirak.devjournal.*`
- Entry types: MANUAL, AUTO_COMMIT, AUTO_BRANCH, STANDUP, PR_SUMMARY
- Storage: `.dev-journal/` v project root (Git-tracked JSON soubory per branch)
- Conventional commits: `feat:`, `fix:`, `chore:`, `refactor:`, `docs:`, `test:`, `perf:`
- Texty v `messages/DevJournalBundle.properties`
- Verzování: `2026.2.X`
- Dependencies: `com.intellij.modules.platform`, optional `Git4Idea`

## Production-grade požadavky
- Všechny I/O operace na background threadech (`Task.Backgroundable`, coroutines)
- Read/Write actions správně (`ReadAction.run {}`, `WriteAction.run {}`)
- `Disposable` pattern pro listeners a services
- Exception handling: žádné unhandled exceptions, graceful degradation
- Logging: `Logger.getInstance(MyClass::class.java)` v každé třídě
- EDT nesmí být blokován — žádné síťové/git/file operace na EDT
- Smart mode check (`DumbService.isDumb()`) kde potřeba

## Freemium architektura
- `FreemiumGate` centrální guard: `FreemiumGate.isPaidFeatureAvailable("feature.id")`
- JetBrains Marketplace Licensing API přes `<product-descriptor>` v `plugin.xml`
- Free tier: lokální poznámky, max 50 poznámek/projekt
- Pro tier: sync, unlimited poznámky, team sharing, standup generator
- Enterprise tier: audit trail, compliance export
- Trial: 30 dní plný přístup, pak downgrade na free tier
- Upgrade prompt: max 1× denně, non-intrusive balloon

## CI/CD
- GitHub Actions: `./gradlew check`, `verifyPlugin`, `runPluginVerifier`
- Detekt pro statickou analýzu Kotlin kódu
- Tag-based release → `publishPlugin` na Marketplace
- Kotlin coding conventions (JetBrains official style)

## Ikona
- `src/main/resources/META-INF/pluginIcon.svg` (40×40, light)
- `src/main/resources/META-INF/pluginIcon_dark.svg` (40×40, dark)
- Action ikony: 16×16 + 13×13 SVG (light + dark varianty)

## Freemium hranice
- Free: lokální poznámky, 50 poznámek/projekt, commit linking, blame view
- Pro: sync, unlimited, team sharing, standup generator, PR summary generator
- Enterprise: audit trail, compliance export, admin dashboard

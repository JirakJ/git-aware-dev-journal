# Changelog

## [2026.2.2] - 2026-02-13

### Changed
- Expanded compatibility range: sinceBuild 232.0+ (IntelliJ 2023.2+)
- Removed upper version bound for future IDE compatibility


## [2026.2.1] - 2026-02-13

### Added
- Initial release
- Commit-linked journal entries with manual and auto-capture support
- Conventional commits parsing (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`, `perf:`)
- Automatic entry categorization (FEATURE, BUGFIX, MAINTENANCE, REFACTOR, DOCS, TEST, PERFORMANCE)
- Branch narrative generation — automatic story of work done on a feature branch
- Timeline view in the **Dev Journal** tool window with filtering and search
- Git blame integration — journal entries visible as an extra column in blame view
- Code hover — "Why did I write this?" context on hover over modified code
- Add Journal Entry action with keyboard shortcut (<kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>J</kbd>)
- Editor context menu integration for adding entries
- JSON-based storage in `.dev-journal/` directory (Git-tracked, merge-friendly)
- Configurable auto-capture, standup time window, and storage path
- Standup generator — "What I did yesterday" from commits and notes (Pro)
- PR/MR summary generator with grouped output by type (Pro)
- Cross-device sync and team sharing (Pro)
- Advanced search, filters, and export to Markdown/JSON/CSV (Pro)

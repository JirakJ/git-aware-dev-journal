# Git-Aware Dev Journal

[![Build](https://github.com/JirakJ/git-aware-dev-journal/actions/workflows/verify.yml/badge.svg)](https://github.com/JirakJ/git-aware-dev-journal/actions/workflows/verify.yml)
[![Version](https://img.shields.io/badge/version-2026.9.0-blue.svg)](https://github.com/JirakJ/git-aware-dev-journal/releases)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue)](LICENSE)

> **Free & open source** — every feature is unlocked for everyone, no license needed. Licensed under [Apache-2.0](LICENSE).

> IDE-native development journal with deep Git integration — every note linked to commits, branches, and code.

## ✨ Features

- **Journal entries** — Note, Decision, TODO, Blocker, Insight, Question, with comma-separated tags
- **Git-linked** — each entry records the current branch and HEAD commit (Git plugin optional) plus file and line
- **Dev Journal tool window** — sortable table with branch filter and text/tag search; refreshes when entries are added
- **Copy Standup** — Markdown standup of the entries from the configured time window, copied to the clipboard
- **Copy Branch Summary** — all entries recorded on the selected branch, grouped by type
- **Plain JSON storage** — `.dev-journal/entries.json` in the project, easy to commit and share with the team

## 📦 Installation

### From JetBrains Marketplace
1. Open **Settings → Plugins → Marketplace**
2. Search for **Git-Aware Dev Journal**
3. Click **Install** and restart the IDE

### From ZIP
1. Download the latest release from [Releases](https://github.com/JirakJ/git-aware-dev-journal/releases)
2. Open **Settings → Plugins → ⚙️ → Install Plugin from Disk**
3. Select the downloaded ZIP file

## 🚀 Quick Start

1. Press <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>J</kbd> in the editor (or right-click → **Add Journal Entry**, or **Tools → Add Journal Entry**)
2. Pick a type, write the entry, optionally add tags — the current file/line, branch and HEAD commit are recorded
3. Open the **Dev Journal** tool window (bottom panel) to browse, filter and search your entries

## ⚙️ Configuration

Open **Settings → Tools → Git-Aware Dev Journal**.

| Setting | Default | Description |
|---------|---------|-------------|
| Standup time window (hours) | `24` | How far back **Copy Standup** looks (1–168) |
| Journal directory | `.dev-journal` | Where `entries.json` is stored — relative to the project root or absolute; applied when the project is reopened |

## 🎯 Usage

### Standup
Click **Copy Standup** in the tool window. Entries newer than the configured window are grouped by type, with branch and
short commit hash, and a "Branches worked on" section; the Markdown is copied to the clipboard.

### Branch summary
Select a branch in the branch filter and click **Copy Branch Summary** to copy all entries recorded on that branch in
chronological order, grouped by type.

### Storage
All entries are stored in a single pretty-printed JSON file, `<journal directory>/entries.json`. Commit the directory to
share the journal with your team, or add it to `.gitignore` to keep it private. Unreadable or corrupt files are logged
and the journal starts empty rather than failing.

## 🔌 Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>J</kbd> | Add Journal Entry |

## 📋 Requirements

- IntelliJ IDEA 2023.2+ (or another IntelliJ-based IDE, build 232+)
- Git4Idea (optional — Git features degrade gracefully without it)

## 🏗️ Building from Source

```bash
git clone https://github.com/JirakJ/git-aware-dev-journal.git
cd git-aware-dev-journal
./gradlew build
```

## License

Apache License 2.0 — see [LICENSE](LICENSE) and [NOTICE](NOTICE). Copyright 2026 Jakub Jirák.

## Contributing

Issues and pull requests are welcome at https://github.com/JirakJ/git-aware-dev-journal.

## 👤 Author

**Jakub Jirák** — [@JirakJ](https://github.com/JirakJ)

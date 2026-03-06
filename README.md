# Git-Aware Dev Journal

[![Build](https://github.com/JirakJ/git-aware-dev-journal/actions/workflows/build.yml/badge.svg)](https://github.com/JirakJ/git-aware-dev-journal/actions/workflows/build.yml)
[![Version](https://img.shields.io/badge/version-2026.2.1-blue.svg)](https://github.com/JirakJ/git-aware-dev-journal/releases)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

> IDE-native development journal with deep Git integration — every note linked to commits, branches, and code.

## ✨ Features

- **Journal entries** — notes, decisions, TODOs, blockers, insights, questions
- **Git-linked notes** — every entry tied to a commit hash, branch, and file position
- **Auto-capture** — parses conventional commits (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`, `perf:`)
- **Branch narratives** — automatic story of work done on a branch
- **Timeline view** — chronological overview with filtering and search in the Dev Journal tool window
- **Commit linking & blame view** — notes visible in Git blame as an extra column
- **Code hover** — "Why did I write this?" context on hover
- **Standup generator** — "What I did yesterday" from commits + notes (Markdown, plain text, Slack-compatible)
- **PR/MR summary generator** — release notes grouped by type (features, fixes, chores)
- **Unlimited notes** — no cap on journal entries per project
- **Cross-device sync** — sync notes between machines
- **Team sharing** — shared `.dev-journal/` directory
- **Advanced search & filters** — by author, branch, date range, entry type
- **Export** — Markdown, JSON, CSV

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

1. Open the **Dev Journal** tool window (bottom panel)
2. Press <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>J</kbd> to add a journal entry at the current cursor position
3. The entry is automatically linked to the current HEAD commit and branch
4. Enable auto-capture to automatically create entries from conventional commits

## ⚙️ Configuration

Open **Settings → Tools → Git-Aware Dev Journal** to configure the plugin.

| Setting | Default | Description |
|---------|---------|-------------|
| `autoCaptureEnabled` | `true` | Automatically capture entries from conventional commits |
| `standupTimeWindow` | `24` | Time window in hours for standup generation (e.g., 24 = last 24 hours) |
| `storagePath` | `.dev-journal` | Directory for journal storage relative to project root |

## 🎯 Usage

### Adding a Journal Entry
- Press <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>J</kbd> anywhere in the editor
- Or right-click → **Add Journal Entry** from the editor context menu
- Or use **Tools → Add Journal Entry**
- Each entry is linked to the current commit, branch, and file/line position

### Auto-Capture from Conventional Commits
When enabled, the plugin automatically creates journal entries from conventional commit messages:
- `feat:` → FEATURE entry
- `fix:` → BUGFIX entry
- `refactor:` → REFACTOR entry
- `docs:` → DOCS entry
- `test:` → TEST entry
- `perf:` → PERFORMANCE entry

### Branch Narratives
Navigate to a feature branch in the Dev Journal tool window to see an automatically generated story of all work done on that branch — commits, notes, and statistics.

### Code Hover — "Why Did I Write This?"
Hover over any line of code to see journal entries from when that code was last modified, including commit context, author, and attached notes.

### Standup Generator
Generate a "What I did yesterday" report from your commits and notes. Configurable time range and output format (Markdown, plain text, Slack-compatible).

### Storage
Journal data is stored in `.dev-journal/` in your project root as Git-tracked JSON files (one file per branch). Lock files are automatically added to `.gitignore`.

## 🔌 Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>J</kbd> | Add Journal Entry |

## 📋 Requirements

- IntelliJ IDEA 2024.3+ (or any JetBrains IDE based on IntelliJ Platform 2024.3+)
- Git4Idea (optional — Git features degrade gracefully without it)
- JVM 17+

## 🏗️ Building from Source

```bash
git clone https://github.com/JirakJ/git-aware-dev-journal.git
cd git-aware-dev-journal
./gradlew build
```

## 📝 License

MIT — [LICENSE](LICENSE)

## 👤 Author

**Jakub Jirák** — [@JirakJ](https://github.com/JirakJ)

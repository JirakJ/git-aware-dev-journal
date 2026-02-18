# Git-Aware Dev Journal

**Remember why you wrote that code, not just what it does.**

---

## The Problem

Developers make dozens of decisions every day — why a certain approach was chosen, what blockers were encountered, what trade-offs were made. This context lives in your head and vanishes when you switch branches, attend meetings, or move to another project. Code comments can't capture the full story, and commit messages are too brief.

---

## Key Features

### Git-Linked Notes
Every journal entry is automatically linked to the current commit hash, branch, and file position. Your notes stay connected to the exact code context they describe.

### Six Entry Types
Organize your thoughts with structured entry types: Notes, Decisions, TODOs, Blockers, Insights, and Questions. Each type has its own icon and filtering.

### Auto-Capture from Commits
Conventional commits (feat:, fix:, docs:, refactor:, test:, chore:, perf:) are automatically parsed and added to your journal. No manual entry needed for standard work.

### Branch Narratives
The plugin generates a read-only narrative of your work on each branch — a timeline of what you did and why, assembled from your entries and commits.

### Timeline View
Browse all journal entries chronologically with full-text search and filters by entry type, branch, date, and author.

### Code Hover Context
Hover over annotated code to see the journal entry explaining why it was written. Answer "Why did I write this?" months later.

### Standup Generator (Pro)
One click generates a standup report from your recent journal entries in Markdown or Slack-compatible format.

### PR Summary Generator (Pro)
Create comprehensive pull request descriptions from your journal entries. No more writing PR descriptions from memory.

---

## How It Works

1. **Add entries as you code** — Press `Ctrl+Shift+J` to add a journal entry at your current cursor position.
2. **Entries auto-link to git** — Each entry captures the current commit, branch, and file location automatically.
3. **Conventional commits are captured** — Standard commit messages are parsed into journal entries without manual effort.
4. **Review and share** — Use the timeline view, generate standups, or export entries for team sharing.

---

## Pricing

| Feature | Free | Pro ($5/month) |
|---------|------|-----------------|
| Git-linked journal entries | ✅ | ✅ |
| 6 entry types | ✅ | ✅ |
| Auto-capture from commits | ✅ | ✅ |
| Branch narratives | ✅ | ✅ |
| Timeline view | ✅ | ✅ |
| Code hover context | ✅ | ✅ |
| Blame view integration | ✅ | ✅ |
| Up to 50 notes per project | ✅ | ✅ |
| Unlimited notes | ❌ | ✅ |
| Standup generator | ❌ | ✅ |
| PR/MR summary generator | ❌ | ✅ |
| Cross-device sync | ❌ | ✅ |
| Team sharing | ❌ | ✅ |
| Advanced search and filters | ❌ | ✅ |
| Export (Markdown/JSON/CSV) | ❌ | ✅ |

---

## Testimonials

> *"The branch narratives alone are worth it. I can pick up where I left off after a week away."*
> — **Full-Stack Developer** *(placeholder)*

> *"Our PR descriptions went from 'fixed stuff' to detailed explanations, thanks to the journal-to-PR generator."*
> — **Tech Lead** *(placeholder)*

---

## FAQ

**Q: Where are journal entries stored?**
A: Entries are stored as JSON files in a `.dev-journal/` directory, one file per branch. This can be committed to your repo for team sharing (Pro).

**Q: Does it work with non-conventional commits?**
A: Yes. Auto-capture works with conventional commits, but you can always add manual entries with `Ctrl+Shift+J` regardless of commit style.

**Q: Can team members see each other's notes?**
A: Pro tier supports team sharing via the `.dev-journal/` directory. When committed to the repo, entries are visible to all team members.

**Q: Will it clutter my repository?**
A: Lock files are automatically added to `.gitignore`. You control whether the journal directory is committed or kept local.

**Q: What IDEs are supported?**
A: All JetBrains IDEs (IntelliJ IDEA, WebStorm, PyCharm, etc.) version 2024.1 and later.

---

## Get Started

Install Git-Aware Dev Journal from the JetBrains Marketplace and start capturing the context behind your code.

[Install from JetBrains Marketplace](#) | [View Documentation](#) | [Report Issues](#)

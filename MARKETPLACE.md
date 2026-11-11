# Git-Aware Dev Journal — JetBrains Marketplace Listing

## Plugin Name
Git-Aware Dev Journal

## Tagline
Never lose the "why" behind your code — journal entries linked to your Git context.

## Short Description
Capture notes, decisions, blockers and TODOs while you code. Each entry records the current branch, HEAD commit and
file/line, is stored as JSON in your project, and can be turned into a standup or branch summary.

## Full Description

<p>
Git blame tells you <strong>who</strong> and <strong>when</strong>, but never <strong>why</strong>.
Git-Aware Dev Journal lets you write down the reasoning while it is fresh and links it to the Git context you were in.
</p>

<h3>Features</h3>
<ul>
  <li><strong>Git-linked entries</strong> — every entry records the current branch and HEAD commit (when the Git plugin is enabled) plus the file and line you were on.</li>
  <li><strong>Six entry types</strong> — Note, Decision, TODO, Blocker, Insight, Question — with free-form comma-separated tags.</li>
  <li><strong>Dev Journal tool window</strong> — sortable table of all entries with a branch filter and text/tag search.</li>
  <li><strong>Copy Standup</strong> — Markdown standup of the entries from the last N hours (configurable), copied to the clipboard.</li>
  <li><strong>Copy Branch Summary</strong> — summary of everything recorded on the selected branch, grouped by entry type.</li>
  <li><strong>Plain JSON storage</strong> — entries live in <code>.dev-journal/entries.json</code> (configurable), so you can commit and share them with your team.</li>
</ul>

<h3>Free &amp; Open Source</h3>
<p>Every feature is free for everyone. The source code is available under the Apache-2.0 license at <a href="https://github.com/JirakJ/git-aware-dev-journal">github.com/JirakJ/git-aware-dev-journal</a>. Issues and pull requests are welcome.</p>

<h3>Getting Started</h3>
<ol>
  <li>Press <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>J</kbd> in the editor (or <em>Tools → Add Journal Entry</em>) to add an entry for the current position.</li>
  <li>Open the <em>Dev Journal</em> tool window to browse, filter and search entries, or copy a standup / branch summary.</li>
</ol>

<h3>Compatibility</h3>
<p>IntelliJ IDEA 2023.2+ and other IntelliJ-based IDEs. The Git plugin is optional; without it entries simply carry no branch/commit.</p>

## Tags
git, journal, documentation, standup, developer-tools

## Category
Version Control

## Pricing

Free & open source (Apache-2.0) — every feature is available to everyone, no license needed.

## Screenshots
1. **Dev Journal tool window** — entries of several types with branch filter and search.
2. **Add Journal Entry dialog** — type, text, tags and the captured file/line.
3. **Copied standup** — the Markdown standup pasted into a chat or document.

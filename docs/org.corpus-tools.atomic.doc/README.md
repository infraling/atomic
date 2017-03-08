# Atomic documentation

The Atomic documentation consists of two parts: user documentation, and developer documentation.

## User documentation

User documentation is made available as 

- a PDF file that is hosted on a GitHub page, 
and is additionally packaged into every Atomic release artifact, 
where it can be found in the root directory after extracting the archive file.
- an HTML website, which is hosted on a GitHub page.
- an Eclipse Help artifact (HTML pages), which is packaged into every Atomic release artifact,
and can be accessed from within the application via the Help menu.

## Developer documentation

Developer documentation is made available as

- an HTML website, hosted on a GitHub page.

## Workflow

Apart from the auto-generated API documentation, all documentation for Atomic is written in [Markdown](https://en.wikipedia.org/wiki/Markdown).

The sources for the **user documentation** are found in `src/main/pandoc`.

The sources for the **developer documentation** are found in `src/main/doxygen`.

### Workflow

#### User documentation

User documentation is written in chapters, where the Markdown file should be named `[dd]-[chaptername].md`, e.g., `01-introduction.md`.
Note that all files to be included in the documentation must be added to the pandoc build in `pom.xml`, cf. comments there.

The Maven build for the user documentation itself is as follows

| Maven phase | Build execution |
---
| `clean` ||
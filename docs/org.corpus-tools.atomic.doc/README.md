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

Apart from the auto-generated API documentation, all documentation for Atomic is written in [Markdown](https://en.wikipedia.org/wiki/Markdown). Note that all documentation Markdown files must end with a blank line.

The sources for the **user documentation** are found in `src/main/pandoc`.

The sources for the **developer documentation** are found in `src/main/doxygen`.

### User documentation

User documentation is written in chapters, where the Markdown file should be named `[dd]-[chaptername].md`, e.g., `01-introduction.md`, where `[dd]` represents the toc level.
Note that all files to be included in the documentation must be added to the pandoc build in `pom.xml` in two places (for HTML and PDF), cf. comments there.

## Build

**Requirements**

- `pandoc` >= v1.12.2.1 on PATH
- `doxygen` >= v1.8.11 on PATH
- `pdflatex` on PATH

### User documentation

The build is triggered by calling `mvn clean package -P docs` in this folder.

The Maven build for the user documentation does the following.

| Maven phase | Build execution |
|---|---|
| `clean` | Remove the old Eclipse help files from the core plugin |
| `generate-sources` | Build DocBook from Markdown via pandoc |
|| Build PDF from Markdown via pandoc |
|| Copy generated PDF to target directory structure |
| `process-sources` | Generate HTML from generated DocBook |
|  | Generate Eclipse Help files from generated DocBook |
| `compile` | Copy generated Eclipse Help files to core plugin |
| `prepare-package` | Change the file references in `toc.xml` (Eclipse Help) to point to correct sub-directory (using a script `clean-toc.sh`)|

### Developer documentation

The Maven build for the developer documentation does the following.

| Maven phase | Build execution |
|---|---|
| `generate-sources` | Replace Maven variables in Doxygen sources with actual values (via filtering in Maven Resources Plugin) |
| `compile` | Generate HTML with Doxygen |
| `prepare-package` | Clean unneeded files from Doxygen build |

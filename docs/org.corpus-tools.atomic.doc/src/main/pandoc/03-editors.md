# Editors

## Grid Editor

The *Grid Editor* is a simple, table-like editor for editing tokens and spans.

[IMAGE HERE]

It works on documents and is opened by right-clicking a `<document-name>.salt` file in the Navigation view > *Open with* > *Grid Editor*.

The *Grid Editor* only displays tokens and spans over tokens.

The currently implemented operations are available via right-click on cells in the grid. Some are activated based on rules, such as the column clicked on, the number or content of selected cells, etc.

The currently available operations are:

- **Annotation**: Annotations can be made in all cells but those in column "Token". To annotate a cell, activate the cell editor (via click, space key, or simply by typing), enter the annotation value (for the key which is also the column header), and press Return to commit.
- **New annotation column**: Create a new annotation column, keyed with the annotation key. This is queried from the user through an input dialog.
- **Delete cell annotation**: Deletes he annotation in the cell the user has clicked on to trigger the command.
- **Delete selected**: Deletes the annotations in the selected cells.
- **Create new span**: Creates a span over the selected cells in one column. If the selection is discontinuous, the span will also be discontinuous, and annotations (or other actions) made in any region of the span will affect the whole span.
- **Split span**: Splits a span into single cells and commits the annotation value of the span to all of them.
- **Create new token**: Creates a new token *below* the clicked-on. The token text will be queried from the user, as well as whether the token should be pre- and suffixed with additional whitespaces. New "null tokens", i.e., tokens covering text of length 0 (i.e., a point between characters) in the primary text, are displayed as `∅`.
- **Create new first token**: Active when clicking on the first token only. Will create a new token *above* the first one. Same functionality as above.
- **Delete token**: Deletes the clicked-on token.
- **Merge token**: Merges tokens. Note that all annotations on the single tokens are deleted in the process.
- **Split token**: Splits a token. The split indices are queried in a dialogue. Note that all annotations on the original token are deleted.

### Notes and outlook

- In its current iteration, the *Grid Editor* does not support annotation namespaces. These will be introduced later.
- In its current iteration, the *Grid Editor* does not support multiple segmentations/timelines. These features will be introduced later.

## Notes on span *merging* vs span *creation*scenarios

Spans shold only be **merged**, when one of the selected cells already contains
an SAnnotation with `value != null`!

Else, a new (visual) span should be created (in the Salt model, this will create
a span with an SAnnotation with the header's `namespace` and `name` and a `value`
of `null`).

Note: if an empty cell has been touched before, its value will be a span! Equally,
visually merged cells contain a span! Therefore, a new span should *only* be
"blindly" created when all cells contain `null` values. Otherwise, spans with
SAnnotations with `value == null` (see above) must be deleted in the process.

**TODO** Decide whether this scenario calls for an extra command and handler, or
whether this can be done in the CreateSpanHandler!

## Notes on coordinates

These tests are running on xvfb, i.e. not on a proper desktop environment.
Therefore, the y coordinates of cells in the grid editor are slightly different to, e.g., those on Unity.

When the `simple-corpus` project is set up and opened in the grid editor, the following values apply (cf. src/test/resources/purgescreen.png):

- initial no. of rows: 12
- Indices: x=150
Is: 31
this 52
example 73
more 94
complicated 115
than 136
it 157
appears 179
to 200
be 221
? 240
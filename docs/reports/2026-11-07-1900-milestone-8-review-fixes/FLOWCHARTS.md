# FLOWCHARTS

## Borrow with race backstop
```mermaid
flowchart TD
    A[POST /api/loans {bookId, memberId}] --> B[Validate book & member exist]
    B --> C[Query open loan: ReturnedAt == null for bookId]
    C --> D{Open loan?}
    D -->|yes| E[Return 409: already on loan]
    D -->|no| F[Insert Loan row]
    F --> G{SaveChanges ok?}
    G -->|yes| H[Return 201 LoanDto]
    G -->|DbUpdateException| I[Unique index violated → Return 409]
    I --> J[Concurrent borrow lost; second borrower gets 409]
```

## Delete book with guard
```mermaid
flowchart TD
    A[POST /Books/DeleteConfirmed/{id}] --> B[Find book by id]
    B --> C{Book exists?}
    C -->|no| D[Return 404]
    C -->|yes| E[Query open loan: BookId=id AND ReturnedAt IS NULL]
    E --> F{Open loan?}
    F -->|yes| G[Block delete → redirect to Index]
    F -->|no| H[Remove book + save changes]
    H --> I[Redirect to Index]
```

## No database diagram change
No tables/columns changed — only a new index. No `erDiagram` required.

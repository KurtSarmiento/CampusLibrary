# FLOWCHARTS

## Feature logic (borrow flow)
```mermaid
flowchart TD
    A[User opens book detail] --> B[BookViewModel.loadBook(id)]
    B --> C{Loading?}
    C -->|yes| D[Show loading indicator]
    C -->|no| E{Result?}
    D --> E
    E -->|Success| F[Render book title/author/ISBN/year + Borrow button]
    E -->|Error| G[Show error message + Retry]
    G -->|Retry| B
    F --> H[User taps Borrow]
    H --> I[ViewModel.borrowBook(bookId, memberId=1)]
    I --> J[Repository POST /api/loans]
    J --> K{Response?}
    K -->|201 Created| L[Show success / navigate to My Loans]
    K -->|409 Conflict| M[Show "This book is already on loan"]
    K -->|Other / network fail| N[Show error message + Retry]
    M --> N
```

## Feature logic (book list load)
```mermaid
flowchart TD
    A[Screen enters composition] --> B[BookViewModel.loadBooks()]
    B --> C[Set state Loading]
    C --> D[Repository GET /api/books]
    D --> E{Success?}
    E -->|yes & non-empty| F[Show book list]
    E -->|yes & empty| G[Show "No books available"]
    E -->|no / exception| H[Show error + Retry]
    H -->|Retry| B
```

## Sequence diagram
```mermaid
sequenceDiagram
    participant User
    participant Screen as BookListScreen / BookDetailScreen
    participant VM as BookViewModel
    participant Repo as BookRepository
    participant API as ApiService / Retrofit
    participant WebApi as CampusLibrary.WebApi
    participant DB as MSSQL CampusLibraryDb

    User->>Screen: taps View on a book
    Screen->>VM: loadBook(id)
    VM->>Repo: getBook(id)
    Repo->>API: GET /api/books/{id}
    API->>WebApi: HTTP GET
    WebApi->>DB: query Books by Id
    DB-->>WebApi: Book row
    WebApi-->>API: 200 BookDto
    API-->>Repo: Response
    Repo-->>VM: Result.Success(book)
    VM->>Screen: update StateFlow → render detail
    User->>Screen: taps Borrow
    Screen->>VM: borrowBook(id, memberId=1)
    VM->>Repo: borrowBook(id, 1)
    Repo->>API: POST /api/loans {bookId, memberId}
    API->>WebApi: HTTP POST
    WebApi->>DB: check open loan; insert Loan
    DB-->>WebApi: result
    WebApi-->>API: 201 LoanDto (or 409)
    API-->>Repo: Response
    Repo-->>VM: Result / error
    VM-->>Screen: callback onSuccess / onError
    Screen->>User: show result / navigate to My Loans
```

## Database (no change)
No schema changed in this milestone, so no `erDiagram` is required.

# Flowcharts: Web MVC Admin Pages

## 1. Feature Logic Flowchart

```mermaid
flowchart TD
    A[Start] --> B{User Action}
    B -->|Books| C[Books Controller]
    B -->|Members| D[Members Controller]
    B -->|Loans| E[Loans Controller]
    
    C --> F[Book CRUD Operations]
    F --> G[Validation]
    G -->|Valid| H[Database Update]
    G -->|Invalid| I[Error Response]
    
    D --> J[Member List/Create]
    J --> K[Validation]
    K -->|Valid| L[Database Update]
    K -->|Invalid| M[Error Response]
    
    E --> N[Loan List/Return]
    N --> O[Loan Rules Check]
    O -->|Valid| P[Database Update]
    O -->|Invalid| Q[Error Response]
    
    H --> R[Success Response]
    L --> R
    P --> R
    
    I --> S[Error Page]
    M --> S
    Q --> S
    
    R --> T[Redirect to View]
    S --> U[Show Error]
```

## 2. Sequence Diagram

```mermaid
sequenceDiagram
    participant User
    participant MVC
    participant Controller
    participant DbContext
    participant Database
    
    User->>MVC: Request page
    MVC->>Controller: Route request
    Controller->>DbContext: Query data
    DbContext->>Database: Execute SQL
    Database-->>DbContext: Return data
    DbContext-->>Controller: Return entities
    Controller->>MVC: Return view with model
    MVC-->>User: Render page
    
    User->>MVC: Submit form
    MVC->>Controller: Route request
    Controller->>DbContext: Update data
    DbContext->>Database: Execute SQL
    Database-->>DbContext: Confirm update
    DbContext-->>Controller: Confirmation
    Controller->>MVC: Redirect or show result
    MVC-->>User: Show result
```

## 3. Database Schema Diagram

```mermaid
erDiagram
    Book ||--o{ Loan : "has"
    Member ||--o{ Loan : "borrows"
    
    Book {
        int Id PK
        string ISBN
        string Title
        string Author
        int PublishedYear
    }
    
    Member {
        int Id PK
        string Name
        string Email
        DateTime JoinedDate
    }
    
    Loan {
        int Id PK
        int BookId FK
        int MemberId FK
        DateTime BorrowedAt
        DateTime ReturnedAt
    }
```

## 4. Book Creation Flow

```mermaid
flowchart TD
    A[Start] --> B[Show Create Form]
    B --> C[User fills form]
    C --> D[Submit Form]
    D --> E[Validate Input]
    E -->|Valid| F[Create Book Entity]
    E -->|Invalid| G[Show Validation Errors]
    F --> H[Save to Database]
    H --> I[Redirect to Books List]
    G --> J[Keep Form Open]
```

## 5. Loan Return Flow

```mermaid
flowchart TD
    A[Start] --> B[Show Open Loans List]
    B --> C[User clicks Return]
    C --> D[Find Loan by ID]
    D --> E[Check if already returned]
    E -->|Not returned| F[Mark as returned]
    E -->|Already returned| G[Show Error]
    F --> H[Update Database]
    H --> I[Redirect to Loans List]
    G --> J[Keep Loan List]
```

## 6. Error Handling Flow

```mermaid
flowchart TD
    A[Start] --> B[User Action]
    B --> C[Controller Action]
    C --> D[Try Operation]
    D -->|Success| E[Return Success]
    D -->|Exception| F[Log Error]
    F --> G[Show Error Page]
    E --> H[Redirect or Show Result]
    G --> I[Show Error Details]
```

## 7. Validation Flow

```mermaid
flowchart TD
    A[Start] --> B[User Input]
    B --> C[Server-Side Validation]
    C -->|Valid| D[Process Data]
    C -->|Invalid| E[Show Errors]
    D --> F[Database Operation]
    E --> G[Keep Form Open]
    F --> H[Success Response]
    G --> I[Keep Form Open]
```

## 8. Anti-Forgery Token Flow

```mermaid
flowchart TD
    A[Start] --> B[Show Form]
    B --> C[Generate Token]
    C --> D[Include in Form]
    D --> E[User Submits Form]
    E --> F[Validate Token]
    F -->|Valid| G[Process Form]
    F -->|Invalid| H[Reject Request]
    G --> I[Success Response]
    H --> J[Show Error]
```

## 9. Bootstrap Styling Flow

```mermaid
flowchart TD
    A[Start] --> B[Render View]
    B --> C[Apply Bootstrap Classes]
    C --> D[Render HTML]
    D --> E[Browser Renders Page]
    E --> F[Apply CSS]
    F --> G[Show Styled Page]
```

## 10. Database Connection Flow

```mermaid
flowchart TD
    A[Start] --> B[Read Connection String]
    B --> C[Create DbContext]
    C --> D[Execute Query]
    D --> E[Handle Results]
    E --> F[Return Data]
    F --> G[Render View]
```

## Key Notes

1. The feature logic flowchart shows the main paths through the admin interface, including validation and error handling.
2. The sequence diagram illustrates the interaction between components when handling requests and database operations.
3. The database schema diagram shows the relationships between the Book, Member, and Loan entities.
4. The book creation and loan return flows show the specific processes for these operations.
5. The error handling and validation flows demonstrate how the system manages these critical aspects of the application.
6. The anti-forgery token and Bootstrap styling flows show how these security and UI features are implemented.
7. The database connection flow shows how the application connects to and interacts with the database.

These flowcharts provide a visual representation of the Web MVC admin interface's architecture and logic, making it easier to understand the flow of data and control through the system.
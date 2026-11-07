# Flowcharts: Web API Books CRUD + Swagger

## 1. API Endpoint Flow

```mermaid
flowchart TD
    Start([Start: API Request]) --> Route[Route to BooksController
/api/books] --> Method{HTTP Method} -->|GET| GetBooks[GET /api/books
List all books] --> ReturnBooks[Return 200 OK
with BookDto array] --> End([End])
    
    Method -->|GET| GetBook[GET /api/books/{id}
Get one book] --> CheckBook{Book Exists?} -->|Yes| ReturnBook[Return 200 OK
with BookDto] --> End
    CheckBook -->|No| Return404[Return 404 Not Found] --> End
    
    Method -->|POST| CreateBook[POST /api/books
Create new book] --> Validate[Validate Input
Check ISBN uniqueness] -->|Valid| SaveBook[Save to Database] --> Return201[Return 201 Created
with BookDto
Location header] --> End
    Validate -->|Invalid| Return400[Return 400 Bad Request
with validation errors] --> End
    Validate -->|Duplicate ISBN| Return409[Return 409 Conflict
"Book with ISBN already exists"] --> End
    
    Method -->|PUT| UpdateBook[PUT /api/books/{id}
Update book] --> CheckID{ID in URL
matches ID in body?} -->|No| Return400ID[Return 400 Bad Request
"ID mismatch"] --> End
    CheckID -->|Yes| ValidateUpdate[Validate Input
Check ISBN uniqueness
(ignore current book)] -->|Valid| UpdateDB[Update in Database] --> Return204[Return 204 No Content] --> End
    ValidateUpdate -->|Invalid| Return400Update[Return 400 Bad Request
with validation errors] --> End
    ValidateUpdate -->|Duplicate ISBN| Return409Update[Return 409 Conflict
"Book with ISBN already exists"] --> End
    
    Method -->|DELETE| DeleteBook[DELETE /api/books/{id}
Delete book] --> CheckDelete{Book Exists?} -->|Yes| RemoveBook[Remove from Database] --> Return204Delete[Return 204 No Content] --> End
    CheckDelete -->|No| Return404Delete[Return 404 Not Found] --> End
    
    style GetBooks fill:#bbdefb
    style GetBook fill:#bbdefb
    style CreateBook fill:#c5e1a5
    style UpdateBook fill:#fff59d
    style DeleteBook fill:#ffccbc
    style Return201 fill:#c8e6c9
    style Return204 fill:#c8e6c9
    style Return400 fill:#ffccbc
    style Return404 fill:#ffccbc
    style Return409 fill:#ffccbc
```

## 2. Book Creation Flow

```mermaid
flowchart TD
    Start([Start: Create Book Request]) --> ValidateInput[Validate Input
ISBN (13 chars), Title (required)] --> CheckISBN{ISBN
Already Exists?} -->|Yes| Reject[Reject Request
Return 409 Conflict] --> End([End])
    
    CheckISBN -->|No| CreateEntity[Create Book Entity
Map DTO to EF entity] --> SaveDB[Save to Database] --> MapDTO[Map EF entity to DTO] --> Return201[Return 201 Created
with BookDto
Location header] --> End
    
    style ValidateInput fill:#fff3e0
    style CheckISBN fill:#fff9c4
    style CreateEntity fill:#fff9c4
    style Return201 fill:#c8e6c9
    style Reject fill:#ffccbc
```

## 3. Book Update Flow

```mermaid
flowchart TD
    Start([Start: Update Book Request]) --> CheckID{ID in URL
matches ID in body?} -->|No| RejectID[Reject Request
Return 400 Bad Request
"ID mismatch"] --> End([End])
    
    CheckID -->|Yes| ValidateInput[Validate Input
ISBN (13 chars), Title (required)] --> CheckISBN{ISBN
Already Exists?
(ignore current book)} -->|Yes| RejectISBN[Reject Request
Return 409 Conflict
"Book with ISBN already exists"] --> End
    
    CheckISBN -->|No| UpdateEntity[Update Book Entity
Map DTO to EF entity] --> SaveDB[Save to Database] --> Return204[Return 204 No Content] --> End
    
    style CheckID fill:#fff3e0
    style ValidateInput fill:#fff3e0
    style CheckISBN fill:#fff9c4
    style UpdateEntity fill:#fff9c4
    style Return204 fill:#c8e6c9
    style RejectID fill:#ffccbc
    style RejectISBN fill:#ffccbc
```

## 4. Book Deletion Flow

```mermaid
flowchart TD
    Start([Start: Delete Book Request]) --> CheckBook{Book Exists?} -->|No| Reject[Reject Request
Return 404 Not Found] --> End([End])
    
    CheckBook -->|Yes| DeleteEntity[Delete Book Entity
Cascade delete loans] --> SaveDB[Save to Database] --> Return204[Return 204 No Content] --> End
    
    style CheckBook fill:#fff9c4
    style DeleteEntity fill:#fff9c4
    style Return204 fill:#c8e6c9
    style Reject fill:#ffccbc
```

## 5. Swagger Integration Flow

```mermaid
flowchart TD
    Start([Start: API Startup]) --> ConfigSwagger[Configure Swagger
AddEndpointsApiExplorer
AddSwaggerGen] --> CheckEnv{Is Development?} -->|Yes| EnableSwagger[Enable Swagger Middleware
UseSwagger
UseSwaggerUI] --> End([End])
    
    CheckEnv -->|No| SkipSwagger[Skip Swagger
in Production] --> End
    
    style ConfigSwagger fill:#fff3e0
    style CheckEnv fill:#fff9c4
    style EnableSwagger fill:#c8e6c9
    style SkipSwagger fill:#ffccbc
```

## 6. CORS Configuration Flow

```mermaid
flowchart TD
    Start([Start: API Startup]) --> ConfigCORS[Configure CORS
AddPolicy for Android Emulator] --> UseCORS[Use CORS Middleware
UseCors("AndroidEmulator")] --> End([End])
    
    style ConfigCORS fill:#fff3e0
    style UseCORS fill:#c8e6c9
```

## 7. Database Access Flow

```mermaid
flowchart TD
    Start([Start: API Request]) --> GetDbContext[Get LibraryDbContext
from DI container] --> QueryDB[Query Database
Books table] --> MapDTO[Map EF entities to DTOs] --> ReturnResponse[Return Response
with DTOs] --> End([End])
    
    style GetDbContext fill:#fff3e0
    style QueryDB fill:#fff9c4
    style MapDTO fill:#fff9c4
    style ReturnResponse fill:#c8e6c9
```

## 8. Error Handling Flow

```mermaid
flowchart TD
    Start([Start: API Request]) --> ProcessRequest[Process Request] --> Error{Error Occurred?} -->|Yes| HandleError[Handle Error
Determine status code] --> ReturnError[Return Error Response
with message] --> End([End])
    
    Error -->|No| ReturnSuccess[Return Success Response] --> End
    
    style HandleError fill:#ffccbc
    style ReturnError fill:#ffccbc
    style ReturnSuccess fill:#c8e6c9
```

## 9. API Lifecycle Flow

```mermaid
flowchart TD
    Start([Start: API Lifecycle]) --> Build[Build Project
`dotnet build src/CampusLibrary.WebApi`] --> Run[Run API
`dotnet run --project src/CampusLibrary.WebApi`] --> Listen[Listen on Port
http://localhost:5238] --> Swagger[Access Swagger UI
http://localhost:5238/swagger] --> Test[Test Endpoints] --> Stop[Stop API
Ctrl+C] --> End([End])
    
    style Build fill:#fff3e0
    style Run fill:#fff9c4
    style Listen fill:#fff9c4
    style Swagger fill:#c8e6c9
    style Test fill:#c8e6c9
    style Stop fill:#ffccbc
```

## 10. Android Emulator Access Flow

```mermaid
flowchart TD
    Start([Start: Android App Request]) --> CORS[CORS Check
Origin: http://10.0.2.2] -->|Allowed| ProcessRequest[Process Request] --> ReturnResponse[Return Response] --> End([End])
    
    CORS -->|Blocked| ReturnCORS[Return CORS Error
403 Forbidden] --> End
    
    style CORS fill:#fff9c4
    style ProcessRequest fill:#fff9c4
    style ReturnResponse fill:#c8e6c9
    style ReturnCORS fill:#ffccbc
```

## 11. Book Entity Relationship Diagram

```mermaid
erDiagram
    BOOKS ||--o{ LOANS : "has"
    
    BOOKS {
        int Id PK
        string ISBN UK "Max 13, Required"
        string Title "Max 200, Required"
        string Author "Max 100, Nullable"
        int PublishedYear
    }
    
    LOANS {
        int Id PK
        int BookId FK
        int MemberId FK
        datetime BorrowedAt "Required"
        datetime ReturnedAt "Nullable - NULL means open loan"
    }
```

## 12. API Testing Flow

```mermaid
flowchart TD
    Start([Start: API Testing]) --> GetAll[GET /api/books
Verify 5 books returned] --> GetOne[GET /api/books/1
Verify correct book returned] --> Create[POST /api/books
Create new book] --> Update[PUT /api/books/6
Update book] --> Delete[DELETE /api/books/6
Delete book] --> VerifyDelete[GET /api/books/6
Verify 404] --> CORS[Test CORS from Android
http://10.0.2.2:5238/api/books] --> End([End])
    
    style GetAll fill:#bbdefb
    style GetOne fill:#bbdefb
    style Create fill:#c5e1a5
    style Update fill:#fff59d
    style Delete fill:#ffccbc
    style VerifyDelete fill:#ffccbc
    style CORS fill:#fff9c4
```

# Spec: CampusLibrary.WebApi (REST API)

## Objective

Build a REST API for the campus library system that exposes JSON endpoints for Books, Members, and Loans. The API serves the Android mobile app and any other HTTP clients. It uses the `CampusLibrary.Data` layer for database access and returns DTOs (never raw EF entities). Implements CRUD for books/members and borrow/return operations for loans.

**Users**: Android app developers, API consumers, testers using Swagger.

**Success**: API runs, Swagger documents all endpoints, Android emulator can call endpoints via `http://10.0.2.2:<port>/api/`, and all business rules are enforced (e.g., cannot borrow a book that's already on loan).

## Tech Stack

- .NET 8 SDK
- ASP.NET Core Web API (controllers, not minimal APIs)
- Swagger/OpenAPI (enabled in Development)
- NuGet packages:
  - `Microsoft.EntityFrameworkCore.SqlServer` (8.x)
  - `Microsoft.EntityFrameworkCore.Design` (8.x, for migrations)
  - `Swashbuckle.AspNetCore` (ships with template)
- Project reference: `CampusLibrary.Data`

## Commands

```bash
# Restore packages
dotnet restore src/CampusLibrary.WebApi

# Build
dotnet build src/CampusLibrary.WebApi

# Run (Development mode, Swagger enabled)
dotnet run --project src/CampusLibrary.WebApi

# Run migrations (WebApi as startup project)
dotnet ef database update -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi

# Watch mode (auto-reload on file changes)
dotnet watch --project src/CampusLibrary.WebApi
```

## Project Structure

```
src/CampusLibrary.WebApi/
├── CampusLibrary.WebApi.csproj       → Web API project, .NET 8
├── Program.cs                         → Minimal hosting, DI setup, CORS, Swagger
├── appsettings.json                   → Base settings
├── appsettings.Development.json       → Connection string, Swagger enabled
├── Controllers/
│   ├── BooksController.cs             → GET/POST/PUT/DELETE /api/books
│   ├── MembersController.cs           → GET/POST /api/members
│   └── LoansController.cs             → POST /api/loans (borrow), POST /api/loans/{id}/return
├── DTOs/
│   ├── BookDto.cs                     → Book response/request DTO
│   ├── MemberDto.cs                   → Member response/request DTO
│   ├── LoanDto.cs                     → Loan response DTO
│   ├── BorrowRequestDto.cs            → { BookId, MemberId }
│   └── ErrorDto.cs                    → Problem details for 400/404/409
└── Properties/
    └── launchSettings.json            → Ports, environment
```

## Code Style

Controller example showing routing, validation, DTOs, and status codes:

```csharp
namespace CampusLibrary.WebApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class BooksController : ControllerBase
{
    private readonly LibraryDbContext _context;

    public BooksController(LibraryDbContext context)
    {
        _context = context;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<BookDto>>> GetBooks()
    {
        var books = await _context.Books.ToListAsync();
        return Ok(books.Select(b => new BookDto
        {
            Id = b.Id,
            ISBN = b.ISBN,
            Title = b.Title,
            Author = b.Author,
            PublishedYear = b.PublishedYear
        }));
    }

    [HttpPost]
    public async Task<ActionResult<BookDto>> CreateBook(BookDto dto)
    {
        if (!ModelState.IsValid)
            return BadRequest(ModelState);

        var book = new Book
        {
            ISBN = dto.ISBN,
            Title = dto.Title,
            Author = dto.Author,
            PublishedYear = dto.PublishedYear
        };

        _context.Books.Add(book);
        await _context.SaveChangesAsync();

        dto.Id = book.Id;
        return CreatedAtAction(nameof(GetBook), new { id = book.Id }, dto);
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<BookDto>> GetBook(int id)
    {
        var book = await _context.Books.FindAsync(id);
        if (book == null)
            return NotFound(new { Message = $"Book {id} not found" });

        return Ok(new BookDto { /* map */ });
    }
}
```

**Conventions**:
- Controllers in `Controllers/` folder, suffixed with `Controller`
- DTOs in `DTOs/` folder, suffixed with `Dto`
- `[ApiController]` + `[Route("api/[controller]")]` on every controller
- `async`/`await` for all DB operations
- Return `ActionResult<T>` from endpoints
- Status codes: `200 Ok`, `201 CreatedAtAction`, `400 BadRequest`, `404 NotFound`, `409 Conflict`
- Problem details JSON for errors: `{ "message": "..." }` or ModelState
- Constructor injection for `LibraryDbContext`

## Testing Strategy

**Unit tests**: Not included in initial milestone (focus on integration verification).

**Integration verification**:
1. Build succeeds: `dotnet build src/CampusLibrary.WebApi`
2. Run API: `dotnet run --project src/CampusLibrary.WebApi`
3. Swagger UI loads at `http://localhost:<port>/swagger`
4. Manual API tests via Swagger or `curl`:
   - `GET /api/books` returns 5 seeded books
   - `POST /api/books` creates a new book, returns 201
   - `POST /api/loans` with valid BookId/MemberId creates loan
   - `POST /api/loans` with same BookId again returns 409 (book already on loan)
   - `POST /api/loans/{id}/return` closes the loan, book becomes available
   - Invalid input returns 400 with ModelState errors

## Boundaries

**Always do**:
- Use DTOs for all request/response bodies (never return EF entities)
- Validate input with data annotations on DTOs
- Return appropriate status codes (200, 201, 400, 404, 409)
- Enable CORS for `http://10.0.2.2` (Android emulator)
- Keep connection string in `appsettings.Development.json`
- Run `dotnet build` and manually test endpoints after every change

**Ask first**:
- Adding authentication/authorization (JWT, cookies, etc.)
- Adding new endpoints beyond Books/Members/Loans CRUD
- Changing error response format beyond `{ "message": "..." }`
- Adding middleware (logging, rate limiting, etc.)

**Never do**:
- Return EF entities directly (always map to DTOs)
- Hard-code connection strings in `Program.cs`
- Skip validation (always check `ModelState.IsValid`)
- Allow borrowing a book that's already on loan (must return 409)
- Commit secrets or API keys

## Success Criteria

- [ ] `CampusLibrary.WebApi.csproj` builds without errors
- [ ] API runs with `dotnet run --project src/CampusLibrary.WebApi`
- [ ] Swagger UI accessible at `/swagger` in Development
- [ ] CORS enabled for `http://10.0.2.2` (Android emulator origin)
- [ ] Endpoints implemented:
  - `GET /api/books` → returns all books as DTO array
  - `GET /api/books/{id}` → returns one book or 404
  - `POST /api/books` → creates book, returns 201 + location header
  - `PUT /api/books/{id}` → updates book, returns 204 or 404
  - `DELETE /api/books/{id}` → deletes book, returns 204 or 404
  - `GET /api/members` → returns all members
  - `POST /api/members` → creates member, returns 201
  - `POST /api/loans` → borrows book (body: `{ "bookId": 1, "memberId": 1 }`), returns 201 or 409 if book on loan
  - `POST /api/loans/{id}/return` → sets `ReturnedAt`, returns 204
- [ ] Business rules enforced:
  - Cannot borrow a book with an existing open loan (409)
  - Invalid input returns 400 with validation errors
  - Missing resource returns 404
- [ ] DTOs defined for all request/response shapes (no raw entities)

## Open Questions

**ASSUMPTIONS I'M MAKING:**

1. **CORS origin**: Android emulator uses `http://10.0.2.2:<port>` (not `localhost`)
2. **Loan endpoint**: `POST /api/loans` body is `{ "bookId": 1, "memberId": 1 }`, returns created loan with Id
3. **Return endpoint**: `POST /api/loans/{id}/return` (not a separate `/api/returns` resource)
4. **Delete book**: Allowed even if loans exist (cascade delete or FK constraint is DB-level concern)
5. **PUT book/member**: Full replacement (not PATCH partial update)
6. **Open loan check**: Query `Loans` where `BookId = x AND ReturnedAt == null` before allowing borrow
7. **Swagger**: Enabled only in Development, not Production
8. **Port**: Auto-assigned by Kestrel (5000-5001 default or from `launchSettings.json`)

→ **Correct me now or I'll proceed with these assumptions.**
